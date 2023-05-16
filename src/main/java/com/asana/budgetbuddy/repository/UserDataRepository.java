package com.asana.budgetbuddy.repository;

import com.asana.budgetbuddy.model.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDataRepository extends JpaRepository<UserData, Long> {
    Optional<UserData> findByUserId(Long id);
}
