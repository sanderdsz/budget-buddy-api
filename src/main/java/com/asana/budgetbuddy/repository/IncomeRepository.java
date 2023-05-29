package com.asana.budgetbuddy.repository;

import com.asana.budgetbuddy.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findAllByUser_Email(String email);
}
