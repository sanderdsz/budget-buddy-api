package com.asana.budgetbuddy.service;

import com.asana.budgetbuddy.dto.LoginDTO;
import com.asana.budgetbuddy.dto.TokenDTO;
import com.asana.budgetbuddy.dto.UserDTO;
import com.asana.budgetbuddy.dto.UserRegistrationDTO;
import com.asana.budgetbuddy.model.User;
import com.asana.budgetbuddy.model.UserData;
import com.asana.budgetbuddy.repository.UserDataRepository;
import com.asana.budgetbuddy.repository.UserRepository;
import com.asana.budgetbuddy.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.Params;
import redis.clients.jedis.params.SetParams;
import redis.clients.jedis.params.XAddParams;

import java.util.Optional;
import java.util.Set;

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

    @Value("${redisUrl}")
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
                // generates a new refresh token
                String refreshToken = jwtUtil.generateRefreshToken(user.get());
                TokenDTO tokenDTO = TokenDTO.builder()
                        .email(dto.getEmail())
                        .refreshToken(refreshToken)
                        .build();
                JedisPool pool = new JedisPool(redisUrl);
                // tries a connection with redis to persist the email / refresh token
                try (Jedis jedis = pool.getResource()) {
                    SetParams params = new SetParams();
                    jedis.set(
                            "user:"+user.get().getEmail()+":email",
                            refreshToken,
                            params.ex(28800)
                    );
                }
                pool.close();
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
            String refreshToken = jwtUtil.generateRefreshToken(savedUser);
            JedisPool pool = new JedisPool(redisUrl);
            // tries a connection with redis to persist the email / refresh token
            try (Jedis jedis = pool.getResource()) {
                SetParams params = new SetParams();
                jedis.set(
                        "user:"+user.get().getEmail()+":email",
                        refreshToken,
                        params.ex(28800)
                );
            }
            pool.close();
            TokenDTO tokenDTO = TokenDTO.builder()
                    .email(savedUser.getEmail())
                    .refreshToken(refreshToken)
                    .build();
            return tokenDTO;
        }
    }
}
