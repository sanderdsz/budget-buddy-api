package com.asana.budgetbuddy.repository;

import com.asana.budgetbuddy.enums.IncomeType;
import com.asana.budgetbuddy.model.Expense;
import com.asana.budgetbuddy.model.Income;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findAllByUser_Email(String email);
    List<Income> findAllByUser_Id(Long id);
    List<Income> findAllByUser_IdAndDateBetweenOrderByDateDesc(Long id, LocalDate startDate, LocalDate endDate);
    List<Income> findAllByUser_IdAndDateAndIncomeTypeOrderByDateDesc(Long id, LocalDate date, IncomeType incomeType, Pageable pageable);
    List<Income> findAllByUser_IdAndDateOrderByDateDesc(Long id, LocalDate date, Pageable pageable);
    List<Income> findAllByUser_IdAndIncomeTypeOrderByDateDesc(Long id, IncomeType incomeType, Pageable pageable);
    List<Income> findAllByUser_IdOrderByDateDesc(Long id, Pageable pageable);
}
