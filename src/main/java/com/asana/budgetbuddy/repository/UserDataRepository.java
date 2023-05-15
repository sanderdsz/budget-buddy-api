package com.asana.budgetbuddy.repository;

import com.asana.budgetbuddy.model.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDataRepository extends JpaRepository<UserData, Long> {
}
