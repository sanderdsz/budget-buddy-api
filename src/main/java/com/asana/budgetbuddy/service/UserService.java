package com.asana.budgetbuddy.service;

import com.asana.budgetbuddy.dto.UserRegistrationDTO;
import com.asana.budgetbuddy.model.User;
import com.asana.budgetbuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public Optional<User> getByEmail(String email) { return userRepository.findByEmail(email); }

    @Transactional
    public User save(UserRegistrationDTO userRegistration) {
        User newUser = User.builder()
                .name(userRegistration.getName())
                .email(userRegistration.getEmail())
                .build();
        userRepository.save(newUser);
        return newUser;
    }

    @Transactional
    public User update(User user) {
        userRepository.save(user);
        return user;
    }
}
