package com.asana.budgetbuddy.service;

import com.asana.budgetbuddy.dto.expense.*;
import com.asana.budgetbuddy.enums.ExpenseType;
import com.asana.budgetbuddy.exception.EntityNotFoundException;
import com.asana.budgetbuddy.model.Expense;
import com.asana.budgetbuddy.user.model.User;
import com.asana.budgetbuddy.repository.ExpenseRepository;
import com.asana.budgetbuddy.util.ExpenseFilter;
import com.asana.budgetbuddy.util.ExpenseFilterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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
    public void delete(Long id) {
        expenseRepository.deleteById(id);
    }

    @Transactional
    public Expense put(ExpenseDTO expenseDTO, User user) {
        Expense newExpense = null;
        try {
            Optional<Expense> expense = expenseRepository.findById(expenseDTO.getId());
            if (expense.isPresent()) {
                newExpense = ExpenseMapper.toModel(expenseDTO, user);
                newExpense.setId(expenseDTO.getId());
                newExpense.setUser(user);
                newExpense.setDate(expenseDTO.getDate());
                newExpense.setValue(expenseDTO.getValue());
                newExpense.setExpenseType(ExpenseType.valueOf(expenseDTO.getExpenseType()));
                newExpense.setDescription(expenseDTO.getDescription());
                expenseRepository.save(newExpense);
            }
        } catch (NoSuchElementException e) {
            throw new EntityNotFoundException("Expense not found");
        }
        return newExpense;
    }

    @Transactional
    public Expense getById(Long id) {
        try {
            Optional<Expense> expense = expenseRepository.findById(id);
            return expense.get();
        } catch (NoSuchElementException e) {
            throw new EntityNotFoundException("Expense not found");
        }
    }

    @Transactional
    public List<ExpenseMonthSummarizeDTO> getMonthlySummarizedByTypeAndValue(
            Long id,
            Integer month
    ) {
        int year = LocalDate.now().getYear();
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

    @Transactional
    public List<Expense> getAllByUserIdPageable(Long id, Pageable pageable) {
        return expenseRepository.findAllByUser_IdOrderByDateDesc(id, pageable);
    }

    @Transactional
    public List<Expense> getAllByUserIdAndExpenseTypePageable(Long id, ExpenseType expenseType, Pageable pageable) {
        return expenseRepository.findAllByUser_IdAndExpenseTypeOrderByDateDesc(id, expenseType, pageable);
    }

    @Transactional
    public List<Expense> getAllByUserIdAndDatePageable(Long id, LocalDate date, Pageable pageable) {
        return expenseRepository.findAllByUser_IdAndDateOrderByDateDesc(id, date, pageable);
    }

    @Transactional
    public List<Expense> getAllByUserIdAndDateAndExpenseTypePageable(Long id, LocalDate date, ExpenseType expenseType, Pageable pageable) {
        return expenseRepository.findAllByUser_IdAndDateAndExpenseTypeOrderByDateDesc(id, date, expenseType, pageable);
    }

    @Transactional
    public List<Expense> getAllUsersChildrenExpenses(
            List<Long> ids,
            LocalDate date,
            ExpenseType expenseType,
            Pageable pageable
    ) {
        List<Expense> expenseList = new ArrayList<>();
        List<Expense> expenses;
        for (Long id : ids) {
            if (date != null & expenseType != null) {
                expenses = expenseRepository.findAllByUser_IdAndDateAndExpenseTypeOrderByDateDesc(id, date, expenseType, pageable);
            } else if (date != null) {
                expenses = expenseRepository.findAllByUser_IdAndDateOrderByDateDesc(id, date, pageable);
            } else if (expenseType != null) {
                expenses = expenseRepository.findAllByUser_IdAndExpenseTypeOrderByDateDesc(id, expenseType, pageable);
            } else {
                expenses = expenseRepository.findAllByUser_IdOrderByDateDesc(id, pageable);
            }
            expenseList.addAll(expenses);
        }
        return expenseList;
    }

    @Transactional
    public List<ExpenseMonthlyDTO> getYearExpenses(Long id) {
        List<ExpenseMonthlyDTO> expenseMonthly = new ArrayList<>();
        int year = LocalDate.now().getYear();
        for (Month month : Month.values()) {
            LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
            LocalDate lastDayOfMonth = LocalDate.of(year, month, firstDayOfMonth.lengthOfMonth());
            List<Expense> expenses = expenseRepository.findAllByUser_IdAndDateBetweenOrderByDateDesc(
                    id,
                    firstDayOfMonth,
                    lastDayOfMonth
            );
            double expenseSum = expenses.stream().mapToDouble(Expense::getValue).sum();
            ExpenseMonthlyDTO monthlyDTO = ExpenseMonthlyDTO
                    .builder()
                    .month(month.getValue())
                    .value(expenseSum)
                    .build();
            expenseMonthly.add(monthlyDTO);
        }
        return expenseMonthly;
    }

    private BigDecimal scaleValue(double value, int scale) {
        return BigDecimal.valueOf(value).setScale(scale, RoundingMode.HALF_UP);
    }

    private BigDecimal calculatePercentage(List<Expense> expenses, List<Expense> expensesFiltered) {
        double totalResult = expenses.stream().mapToDouble(Expense::getValue).sum();
        double expensesFilteredResult = expensesFiltered.stream().mapToDouble(Expense::getValue).sum();
        double resultDifference = (expensesFilteredResult * 100) / totalResult;
        return scaleValue(resultDifference, 0);
    }

    private ExpenseMonthSummarizeDTO monthSummarizeFactory(
            List<Expense> expenses,
            List<Expense> expensesFiltered,
            ExpenseType type
    ) {
        return ExpenseMonthSummarizeDTO.builder()
                .expenses(ExpenseMapper.toDTO(expensesFiltered))
                .expenseType(type.toString())
                .totalValue(scaleValue(expensesFiltered.stream().mapToDouble(Expense::getValue).sum(), 2))
                .percentage(calculatePercentage(expenses, expensesFiltered))
                .build();
    }
}
