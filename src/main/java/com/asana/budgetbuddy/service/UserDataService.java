package com.asana.budgetbuddy.service;

import com.asana.budgetbuddy.dto.UserRegistrationDTO;
import com.asana.budgetbuddy.model.User;
import com.asana.budgetbuddy.model.UserData;
import com.asana.budgetbuddy.repository.UserDataRepository;
import com.asana.budgetbuddy.util.JwtUtil;
import com.asana.budgetbuddy.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

@Service
public class UserDataService {

    @Autowired
    private UserDataRepository repository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("redis://default:398eb0340530405995382151eeab349c@us1-maximum-drake-38997.upstash.io:38997")
    private String redisUrl;

    @Transactional
    public Optional<UserData> getById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public UserData save(UserRegistrationDTO userRegistration, User user) {
        if (!userRegistration.isExternal()) {
            String refreshToken = jwtUtil.generateRefreshToken(user);
            UserData newUserData = UserData
                    .builder()
                    .user(user)
                    .password(passwordEncoder.encode(userRegistration.getPassword()))
                    .refreshToken(refreshToken)
                    .build();
            UserData savedUserData = repository.save(newUserData);
            JedisPool pool = new JedisPool(redisUrl);
            try (Jedis jedis = pool.getResource()) {
                jedis.set(
                        savedUserData.getId().toString(),
                        refreshToken
                );
            }
            pool.close();
            return newUserData;
        }
        return null;
    }

    @Transactional
    public UserData update(User user, String password) {
        String newEncryption = passwordEncoder.encode(password);
        String refreshToken = jwtUtil.generateRefreshToken(user);
        Optional<UserData> currentUserData = repository.findByUser_Id(user.getId());
        currentUserData.get().setPassword(newEncryption);
        currentUserData.get().setRefreshToken(refreshToken);
        repository.save(currentUserData.get());
        JedisPool pool = new JedisPool(redisUrl);
        try (Jedis jedis = pool.getResource()) {
            jedis.set(
                    currentUserData.get().getId().toString(),
                    refreshToken
            );
        }
        pool.close();
        return currentUserData.get();
    }
}

