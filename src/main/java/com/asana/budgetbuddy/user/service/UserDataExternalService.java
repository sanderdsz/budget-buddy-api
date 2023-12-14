package com.asana.budgetbuddy.user.service;

import com.asana.budgetbuddy.user.model.UserDataExternal;
import com.asana.budgetbuddy.user.repository.UserDataExternalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserDataExternalService {

    @Autowired
    private UserDataExternalRepository repository;

    @Transactional
    public Optional<UserDataExternal> getById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public Optional<UserDataExternal> getByUserId(Long id) {
        return repository.findByUser_Id(id);
    }
}
