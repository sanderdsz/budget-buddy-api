package com.asana.budgetbuddy.service;

import com.asana.budgetbuddy.model.Expense;
import com.asana.budgetbuddy.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Transactional
    public Expense save(Expense expense) {
        expenseRepository.save(expense);
        return expense;
    }

    @Transactional
    public List<Expense> getAllByUserEmailAndDateBetween(Long id, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findAllByUser_IdAndDateBetweenOrderByDateDesc(id, startDate, endDate);
    }

    @Transactional
    public List<Expense> getAllByUserEmailAndYearAndMonth(Long id, Integer year, Integer month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = LocalDate.of(year, month, startDate.lengthOfMonth());
        return expenseRepository.findAllByUser_IdAndDateBetweenOrderByDateDesc(id, startDate, endDate);
    }
}
