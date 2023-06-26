package com.asana.budgetbuddy.service;

import com.asana.budgetbuddy.dto.expense.ExpenseMapper;
import com.asana.budgetbuddy.dto.expense.ExpenseMonthSummarizeDTO;
import com.asana.budgetbuddy.enums.ExpenseType;
import com.asana.budgetbuddy.model.Expense;
import com.asana.budgetbuddy.repository.ExpenseRepository;
import com.asana.budgetbuddy.util.ExpenseFilter;
import com.asana.budgetbuddy.util.ExpenseFilterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ExpenseFilterFactory filterFactory;

    @Transactional
    public Expense save(Expense expense) {
        expenseRepository.save(expense);
        return expense;
    }

    @Transactional
    public List<ExpenseMonthSummarizeDTO> getMonthlySummarizedByTypeAndValue(Long id) {
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = LocalDate.of(year, month, startDate.lengthOfMonth());

        List<ExpenseMonthSummarizeDTO> expenseMonthSummarizeDTOList = new ArrayList<>();
        List<Expense> expenseList = expenseRepository.findAllByUser_IdAndDateBetweenOrderByDateDesc(id, startDate, endDate);

        ExpenseFilter mealsFilter = filterFactory.createFilter(ExpenseType.MEALS);
        List<Expense> mealsExpenses = mealsFilter.filter(expenseList);
        if (mealsExpenses.size() != 0) {
            expenseMonthSummarizeDTOList.add(monthSummarizeFactory(expenseList, mealsExpenses, ExpenseType.MEALS));
        }

        ExpenseFilter groceryFilter = filterFactory.createFilter(ExpenseType.GROCERY);
        List<Expense> groceryExpenses = groceryFilter.filter(expenseList);
        if (groceryExpenses.size() != 0) {
            expenseMonthSummarizeDTOList.add(monthSummarizeFactory(expenseList, groceryExpenses, ExpenseType.GROCERY));
        }

        ExpenseFilter housingFilter = filterFactory.createFilter(ExpenseType.HOUSING);
        List<Expense> housingExpenses = housingFilter.filter(expenseList);
        if (housingExpenses.size() != 0) {
            expenseMonthSummarizeDTOList.add(monthSummarizeFactory(expenseList, housingExpenses, ExpenseType.HOUSING));
        }

        ExpenseFilter pharmacyFilter = filterFactory.createFilter(ExpenseType.PHARMACY);
        List<Expense> pharmacyExpenses = pharmacyFilter.filter(expenseList);
        if (pharmacyExpenses.size() != 0) {
            expenseMonthSummarizeDTOList.add(monthSummarizeFactory(expenseList, pharmacyExpenses, ExpenseType.PHARMACY));
        }

        ExpenseFilter shoppingFilter = filterFactory.createFilter(ExpenseType.SHOPPING);
        List<Expense> shoppingExpenses = shoppingFilter.filter(expenseList);
        if (shoppingExpenses.size() != 0) {
            expenseMonthSummarizeDTOList.add(monthSummarizeFactory(expenseList, shoppingExpenses, ExpenseType.SHOPPING));
        }

        ExpenseFilter travelsFilter = filterFactory.createFilter(ExpenseType.TRAVELS);
        List<Expense> travelsExpenses = travelsFilter.filter(expenseList);
        if (travelsExpenses.size() != 0) {
            expenseMonthSummarizeDTOList.add(monthSummarizeFactory(expenseList, travelsExpenses, ExpenseType.TRAVELS));
        }

        ExpenseFilter carFilter = filterFactory.createFilter(ExpenseType.CAR);
        List<Expense> carExpenses = carFilter.filter(expenseList);
        if (carExpenses.size() != 0) {
            expenseMonthSummarizeDTOList.add(monthSummarizeFactory(expenseList, carExpenses, ExpenseType.CAR));
        }

        return expenseMonthSummarizeDTOList;
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

    private BigDecimal scaleValue(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculatePercentage(List<Expense> expenses, List<Expense> expensesFiltered) {
        double totalResult = expenses.stream().mapToDouble(Expense::getValue).sum();
        double expensesFilteredResult = expensesFiltered.stream().mapToDouble(Expense::getValue).sum();
        double resultDifference = (expensesFilteredResult * 100) / totalResult;
        return scaleValue(resultDifference);
    }

    private ExpenseMonthSummarizeDTO monthSummarizeFactory(
            List<Expense> expenses,
            List<Expense> expensesFiltered,
            ExpenseType type
    ) {
        return ExpenseMonthSummarizeDTO.builder()
                .expenses(ExpenseMapper.toDTO(expensesFiltered))
                .expenseType(type.toString())
                .percentage(calculatePercentage(expenses, expensesFiltered))
                .build();
    }
}
