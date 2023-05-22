package com.asana.budgetbuddy.service;

import com.asana.budgetbuddy.dto.LoginDTO;
import com.asana.budgetbuddy.dto.TokenDTO;
import com.asana.budgetbuddy.dto.UserRegistrationDTO;
import com.asana.budgetbuddy.model.User;
import com.asana.budgetbuddy.model.UserData;
import com.asana.budgetbuddy.repository.UserDataRepository;
import com.asana.budgetbuddy.repository.UserRepository;
import com.asana.budgetbuddy.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.Optional;

@Slf4j
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDataRepository userDataRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${REDIS_URL}")
    private String redisUrl;

    public TokenDTO login(LoginDTO dto) {
        // verify the user in the database via email
        Optional<User> user = userRepository.findByEmail(dto.getEmail());
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Username not found");
        } else {
            // get the internal sensitive data from the user
            Optional<UserData> userData = userDataRepository.findByUser_Id(user.get().getId());
            if (passwordEncoder.matches(dto.getPassword(), userData.get().getPassword())) {
                // create a new pool connection with redis
                JedisPool pool = new JedisPool(redisUrl);
                String accessToken = null;
                try (Jedis jedis = pool.getResource()) {
                    // verify if there is a refresh token in redis
                    String existAccessToken = jedis.get("user:" + user.get().getEmail() + ":email");
                    if (existAccessToken != null) {
                        accessToken = existAccessToken;
                    } else {
                        // generates a new refresh token
                        accessToken = jwtUtil.generateRefreshToken(user.get());
                        // set the refresh token to expire in 8 hours
                        SetParams params = new SetParams();
                        // persist the new refresh token into redis
                        jedis.set(
                                "user:" + user.get().getEmail() + ":access_token",
                                accessToken,
                                params.ex(28800)
                        );
                    }
                }
                pool.close();
                TokenDTO tokenDTO = TokenDTO.builder()
                        .email(dto.getEmail())
                        .accessToken(accessToken)
                        .build();
                return tokenDTO;
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is incorrect");
        }
    }

    public TokenDTO save(UserRegistrationDTO userRegistrationDTO) {
        // verify if there is already an email in the database
        Optional<User> user = userRepository.findByEmail(userRegistrationDTO.getEmail());
        if (user.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This e-mail is already registered");
        } else {
            User newUser = User.builder()
                    .name(userRegistrationDTO.getName())
                    .email(userRegistrationDTO.getEmail())
                    .isExternal(false)
                    .build();
            User savedUser = userRepository.save(newUser);
            UserData newUserData = UserData.builder()
                    .user(savedUser)
                    .password(passwordEncoder.encode(userRegistrationDTO.getPassword()))
                    .build();
            userDataRepository.save(newUserData);
            // generates a new refresh token
            String accessToken = jwtUtil.generateRefreshToken(savedUser);
            JedisPool pool = new JedisPool(redisUrl);
            // tries a connection with redis to persist the email / refresh token
            try (Jedis jedis = pool.getResource()) {
                // set the refresh token to expire in 8 hours
                SetParams params = new SetParams();
                jedis.set(
                        "user:" + user.get().getEmail() + ":access_token",
                        accessToken,
                        params.ex(28800)
                );
            }
            pool.close();
            TokenDTO tokenDTO = TokenDTO.builder()
                    .email(savedUser.getEmail())
                    .accessToken(accessToken)
                    .build();
            return tokenDTO;
        }
    }
}
