package com.asana.budgetbuddy.repository;

import com.asana.budgetbuddy.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findAllByUser_Email(String email);
    List<Expense> findAllByUser_Id(Long id);
    List<Expense> findAllByUser_IdAndDateBetweenOrderByDateDesc(Long id, LocalDate startDate, LocalDate endDate);
}
