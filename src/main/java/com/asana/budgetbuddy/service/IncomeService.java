package com.asana.budgetbuddy.service;

import com.asana.budgetbuddy.model.Income;
import com.asana.budgetbuddy.repository.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IncomeService {

    @Autowired
    private IncomeRepository incomeRepository;

    @Transactional
    public Income save(Income income) {
        incomeRepository.save(income);
        return income;
    }

    @Transactional
    public List<Income> getAllByUserEmail(String email) {
        return incomeRepository.findAllByUser_Email(email);
    }
}
