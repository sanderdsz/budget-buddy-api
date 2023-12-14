package com.asana.budgetbuddy.auth.service;

import com.asana.budgetbuddy.auth.dto.GoogleAuthResponseDTO;
import com.asana.budgetbuddy.auth.dto.GoogleUserDTO;
import com.asana.budgetbuddy.auth.dto.LoginDTO;
import com.asana.budgetbuddy.auth.dto.TokenDTO;
import com.asana.budgetbuddy.user.dto.UserRegistrationDTO;
import com.asana.budgetbuddy.user.model.User;
import com.asana.budgetbuddy.user.model.UserData;
import com.asana.budgetbuddy.user.model.UserDataExternal;
import com.asana.budgetbuddy.user.repository.UserDataExternalRepository;
import com.asana.budgetbuddy.user.repository.UserDataRepository;
import com.asana.budgetbuddy.user.repository.UserRepository;
import com.asana.budgetbuddy.shared.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
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
    private UserDataExternalRepository userDataExternalRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${REDIS_URL}")
    private String redisUrl;

    @Transactional
    public TokenDTO login(LoginDTO dto) {
        Optional<User> user = userRepository.findByEmail(dto.getEmail());
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Username not found");
        } else {
            Optional<UserData> userData = userDataRepository.findByUser_Id(user.get().getId());
            if (passwordEncoder.matches(dto.getPassword(), userData.get().getPassword())) {
                JedisPool pool = new JedisPool(redisUrl);
                String accessToken = null;
                try (Jedis jedis = pool.getResource()) {
                    String existAccessToken = jedis.get("user:" + user.get().getEmail() + ":email");
                    if (existAccessToken != null) {
                        accessToken = existAccessToken;
                    } else {
                        accessToken = jwtUtil.generateAccessToken(user.get());
                        SetParams params = new SetParams();
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

    @Transactional
    public TokenDTO save(UserRegistrationDTO userRegistrationDTO) {
        Optional<User> user = userRepository.findByEmail(userRegistrationDTO.getEmail());
        if (user.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This e-mail is already registered");
        } else {
            User newUser = User.builder()
                    .firstName(userRegistrationDTO.getFirstName())
                    .lastName(userRegistrationDTO.getLastName())
                    .email(userRegistrationDTO.getEmail())
                    .isExternal(false)
                    .build();
            User savedUser = userRepository.save(newUser);
            UserData newUserData = UserData.builder()
                    .user(savedUser)
                    .password(passwordEncoder.encode(userRegistrationDTO.getPassword()))
                    .build();
            userDataRepository.save(newUserData);
            String accessToken = jwtUtil.generateAccessToken(savedUser);
            JedisPool pool = new JedisPool(redisUrl);
            try (Jedis jedis = pool.getResource()) {
                SetParams params = new SetParams();
                jedis.set(
                        "user:" + newUser.getEmail() + ":access_token",
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

    @Transactional
    public void logout(TokenDTO tokenDTO) {
        JedisPool pool = new JedisPool(redisUrl);
        try (Jedis jedis = pool.getResource()) {
            jedis.del("user:" + tokenDTO.getEmail() + ":access_token");
        }
    }

    @Transactional
    public TokenDTO verify(TokenDTO tokenDTO) {
        JedisPool pool = new JedisPool(redisUrl);
        try (Jedis jedis = pool.getResource()) {
            String accessToken = jedis.get("user:" + tokenDTO.getEmail() + ":access_token");
            if (accessToken == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access token expired");
            }
            if (accessToken.compareTo(tokenDTO.getAccessToken()) != 0) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access token invalid");
            }
        }
        return tokenDTO;
    }

    @Transactional
    public TokenDTO googleVerification(String googleAccessToken, GoogleUserDTO googleUserDTO) {
        try {
            String googleTokenVerifyUrl = "https://www.googleapis.com/oauth2/v3/tokeninfo?access_token=" + googleAccessToken;
            GoogleAuthResponseDTO googleAuthResponse = new RestTemplate().getForObject(googleTokenVerifyUrl, GoogleAuthResponseDTO.class);
            if (googleAuthResponse != null) {
                String accessToken;
                Optional<User> user = userRepository.findByEmail(googleAuthResponse.getEmail());
                if (user.isPresent()) {
                    accessToken = getRedisToken(user.get());
                    if (accessToken != null) {
                        TokenDTO tokenDTO = TokenDTO.builder()
                                .email(user.get().getEmail())
                                .accessToken(accessToken)
                                .build();
                        return tokenDTO;
                    } else {
                        accessToken = jwtUtil.generateAccessToken(user.get());
                        setRedisToken(user.get(), accessToken);
                        TokenDTO tokenDTO = TokenDTO.builder()
                                .email(user.get().getEmail())
                                .accessToken(accessToken)
                                .build();
                        return tokenDTO;
                    }
                } else {
                    User newUser = User.builder()
                            .firstName(googleUserDTO.getFirstName())
                            .lastName(googleUserDTO.getLastName())
                            .email(googleUserDTO.getEmail())
                            .isExternal(true)
                            .build();
                    User savedUser = userRepository.save(newUser);
                    UserDataExternal newUserData = UserDataExternal.builder()
                            .user(savedUser)
                            .providerName("GOOGLE")
                            .providerId(googleUserDTO.getId())
                            .providerToken(googleAccessToken)
                            .build();
                    UserDataExternal savedUserData = userDataExternalRepository.save(newUserData);
                    accessToken = jwtUtil.generateAccessToken(savedUser);
                    setRedisToken(savedUser, accessToken);
                    TokenDTO tokenDTO = TokenDTO.builder()
                            .email(savedUser.getEmail())
                            .accessToken(accessToken)
                            .build();
                    return tokenDTO;
                }
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error communicating with Google API");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access token invalid");
        }
    }

    private String getRedisToken(User user) {
        JedisPool pool = new JedisPool(redisUrl);
        String existAccessToken;
        try (Jedis jedis = pool.getResource()) {
            existAccessToken = jedis.get("user:" + user.getEmail() + ":access_token");
        }
        pool.close();
        return existAccessToken;
    }

    private void setRedisToken(User user, String accessToken) {
        JedisPool pool = new JedisPool(redisUrl);
        try (Jedis jedis = pool.getResource()) {
            SetParams params = new SetParams();
            jedis.set(
                    "user:" + user.getEmail() + ":access_token",
                    accessToken,
                    params.ex(28800)
            );
        }
        pool.close();
    }
}
