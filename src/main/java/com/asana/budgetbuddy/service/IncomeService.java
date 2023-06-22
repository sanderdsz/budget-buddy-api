package com.asana.budgetbuddy.service;

import com.asana.budgetbuddy.model.Income;
import com.asana.budgetbuddy.repository.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    public List<Income> getAllByUserEmailAndDateBetween(Long id, LocalDate startDate, LocalDate endDate) {
        return incomeRepository.findAllByUser_IdAndDateBetweenOrderByDateDesc(id, startDate, endDate);
    }

    @Transactional
    public List<Income> getAllByUserEmailAndYearAndMonth(Long id, Integer year, Integer month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = LocalDate.of(year, month, startDate.lengthOfMonth());
        return incomeRepository.findAllByUser_IdAndDateBetweenOrderByDateDesc(id, startDate, endDate);
    }
}
