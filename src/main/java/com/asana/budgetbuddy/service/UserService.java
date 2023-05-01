package com.asana.budgetbuddy.service;

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
    public User save(User user) {
        userRepository.save(user);
        return user;
    }
}
