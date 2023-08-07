package com.asana.budgetbuddy.repository;

import com.asana.budgetbuddy.enums.ExpenseType;
import com.asana.budgetbuddy.model.Expense;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findAllByUser_Email(String email);
    List<Expense> findAllByUser_Id(Long id);
    List<Expense> findAllByUser_IdAndDateBetweenOrderByDateDesc(Long id, LocalDate startDate, LocalDate endDate);
    List<Expense> findAllByUser_IdOrderByDateDesc(Long id, Pageable pageable);
    List<Expense> findAllByUser_IdAndExpenseTypeOrderByDateDesc(Long id, ExpenseType expenseType, Pageable pageable);
    List<Expense> findAllByUser_IdAndDateOrderByDateDesc(Long id, LocalDate date, Pageable pageable);
    List<Expense> findAllByUser_IdAndDateAndExpenseTypeOrderByDateDesc(Long id, LocalDate date, ExpenseType expenseType, Pageable pageable);
    List<Expense> findAllByUser_IdAndUser_UserChildren_IdAndDateAndExpenseTypeOrderByDateDesc(Long id, Long childId, LocalDate date, ExpenseType expenseType, Pageable pageable);
}
