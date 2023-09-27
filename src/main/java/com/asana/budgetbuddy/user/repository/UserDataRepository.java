package com.asana.budgetbuddy.user.repository;

import com.asana.budgetbuddy.user.model.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDataRepository extends JpaRepository<UserData, Long> {
    Optional<UserData> findByUser_Id(Long id);
}
