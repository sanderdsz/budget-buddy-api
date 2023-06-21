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
import java.util.List;

@Service
public class BalanceService {

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Transactional
    public BalanceDTO getBalanceByEmail(String email) {
        List<Income> incomes = incomeRepository.findAllByUser_Email(email);
        List<Expense> expenses = expenseRepository.findAllByUser_Email(email);
        double incomeSum = incomes.stream().mapToDouble(Income::getValue).sum();
        double expenseSum = expenses.stream().mapToDouble(Expense::getValue).sum();
        BigDecimal scaledBalance = BigDecimal.valueOf(incomeSum - expenseSum).setScale(2, BigDecimal.ROUND_HALF_UP);
        BalanceDTO balanceDTO = new BalanceDTO(scaledBalance);
        return balanceDTO;
    }

    @Transactional
    public BalanceDTO getBalanceByUserId(Long id) {
        List<Income> incomes = incomeRepository.findAllByUser_Id(id);
        List<Expense> expenses = expenseRepository.findAllByUser_Id(id);
        double incomeSum = incomes.stream().mapToDouble(Income::getValue).sum();
        double expenseSum = expenses.stream().mapToDouble(Expense::getValue).sum();
        BigDecimal scaledBalance = BigDecimal.valueOf(incomeSum - expenseSum).setScale(2, BigDecimal.ROUND_HALF_UP);
        BalanceDTO balanceDTO = new BalanceDTO(scaledBalance);
        return balanceDTO;
    }
}
