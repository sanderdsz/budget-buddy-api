package com.asana.budgetbuddy.service;

import com.asana.budgetbuddy.model.Expense;
import com.asana.budgetbuddy.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    public Optional<Expense> getById(Long id) {
        return expenseRepository.findById(id);
    }
}
