package com.asana.budgetbuddy.user.repository;

import com.asana.budgetbuddy.user.model.UserDataExternal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDataExternalRepository extends JpaRepository<UserDataExternal, Long> {

    Optional<UserDataExternal> findByUser_Id(Long id);
}
