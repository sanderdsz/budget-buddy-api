package com.asana.budgetbuddy.service;

import com.asana.budgetbuddy.enums.IncomeType;
import com.asana.budgetbuddy.exception.EntityNotFoundException;
import com.asana.budgetbuddy.model.Income;
import com.asana.budgetbuddy.repository.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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
    public Income getById(Long id) {
        try {
            Optional<Income> income = incomeRepository.findById(id);
            return income.get();
        } catch (NoSuchElementException e) {
            throw new EntityNotFoundException("Income not found");
        }
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

    @Transactional
    public List<Income> getAllByUserIdAndDateAndIncomeTypePageable(Long id, LocalDate date, IncomeType incomeType, Pageable pageable) {
        return incomeRepository.findAllByUser_IdAndDateAndIncomeTypeOrderByDateDesc(id, date, incomeType, pageable);
    }

    @Transactional
    public List<Income> getAllByUserIdAndIncomeTypePageable(Long id, IncomeType incomeType, Pageable pageable) {
        return incomeRepository.findAllByUser_IdAndIncomeTypeOrderByDateDesc(id, incomeType, pageable);
    }

    @Transactional
    public List<Income> getAllByUserIdAndDatePageable(Long id, LocalDate date, Pageable pageable) {
        return incomeRepository.findAllByUser_IdAndDateOrderByDateDesc(id, date, pageable);
    }

    @Transactional
    public List<Income> getAllByUserIdPageable(Long id, Pageable pageable) {
        return incomeRepository.findAllByUser_IdOrderByDateDesc(id, pageable);
    }
}
