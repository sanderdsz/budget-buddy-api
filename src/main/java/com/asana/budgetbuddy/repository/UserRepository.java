package com.asana.budgetbuddy.repository;

import com.asana.budgetbuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
