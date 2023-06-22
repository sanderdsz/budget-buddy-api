package com.asana.budgetbuddy.service;

import com.asana.budgetbuddy.dto.BalanceDTO;
import com.asana.budgetbuddy.model.Expense;
import com.asana.budgetbuddy.model.Income;
import com.asana.budgetbuddy.repository.ExpenseRepository;
import com.asana.budgetbuddy.repository.IncomeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class BalanceService {

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Transactional
    public BalanceDTO getBalanceByUserIdAndYearAndMonth(Long id, Integer year, Integer month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = LocalDate.of(year, month, startDate.lengthOfMonth());
        List<Income> incomes = incomeRepository.findAllByUser_IdAndDateBetweenOrderByDateDesc(id, startDate, endDate);
        List<Expense> expenses = expenseRepository.findAllByUser_IdAndDateBetweenOrderByDateDesc(id, startDate, endDate);
        double incomeSum = incomes.stream().mapToDouble(Income::getValue).sum();
        double expenseSum = expenses.stream().mapToDouble(Expense::getValue).sum();
        BigDecimal scaledBalance = BigDecimal.valueOf(incomeSum - expenseSum).setScale(2, RoundingMode.HALF_UP);
        BalanceDTO balanceDTO = new BalanceDTO(scaledBalance);
        return balanceDTO;
    }

    @Transactional
    public BalanceDTO getBalanceByUserIdAndDateBetween(Long id, LocalDate startDate, LocalDate endDate) {
        List<Income> incomes = incomeRepository.findAllByUser_IdAndDateBetweenOrderByDateDesc(id, startDate, endDate);
        List<Expense> expenses = expenseRepository.findAllByUser_IdAndDateBetweenOrderByDateDesc(id, startDate, endDate);
        double incomeSum = incomes.stream().mapToDouble(Income::getValue).sum();
        double expenseSum = expenses.stream().mapToDouble(Expense::getValue).sum();
        BigDecimal scaledBalance = BigDecimal.valueOf(incomeSum - expenseSum).setScale(2, RoundingMode.HALF_UP);
        BalanceDTO balanceDTO = new BalanceDTO(scaledBalance);
        return balanceDTO;
    }
}
