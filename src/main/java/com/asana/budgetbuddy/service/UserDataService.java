package com.asana.budgetbuddy.service;

import com.asana.budgetbuddy.model.User;
import com.asana.budgetbuddy.model.UserData;
import com.asana.budgetbuddy.repository.UserDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserDataService {

    @Autowired
    private UserDataRepository repository;

    @Transactional
    public Optional<UserData> getById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public UserData save(UserData userData) {
        repository.save(userData);
        return userData;
    }
}
