package com.asana.budgetbuddy.expense.service;

import com.asana.budgetbuddy.expense.dto.ExpenseDTO;
import com.asana.budgetbuddy.expense.dto.ExpenseMapper;
import com.asana.budgetbuddy.expense.dto.ExpenseMonthSummarizeDTO;
import com.asana.budgetbuddy.expense.dto.ExpenseMonthlyDTO;
import com.asana.budgetbuddy.expense.enums.ExpenseType;
import com.asana.budgetbuddy.shared.exception.EntityNotFoundException;
import com.asana.budgetbuddy.expense.model.Expense;
import com.asana.budgetbuddy.user.model.User;
import com.asana.budgetbuddy.expense.repository.ExpenseRepository;
import com.asana.budgetbuddy.expense.util.ExpenseFilter;
import com.asana.budgetbuddy.expense.util.ExpenseFilterFactory;
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
        return expenseRepository.findById(expenseDTO.getId())
                .map(existingExpense -> {
                    Expense updatedExpense = ExpenseMapper.toModel(expenseDTO, user);
                    updatedExpense.setId(expenseDTO.getId());
                    updatedExpense.setUser(user);
                    updatedExpense.setDate(expenseDTO.getDate());
                    updatedExpense.setValue(expenseDTO.getValue());
                    updatedExpense.setExpenseType(ExpenseType.valueOf(expenseDTO.getExpenseType()));
                    updatedExpense.setDescription(expenseDTO.getDescription());
                    return expenseRepository.save(updatedExpense);
                })
                .orElseThrow(() -> new EntityNotFoundException("Expense not found"));
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

        for (ExpenseType expenseType: ExpenseType.values()) {
            ExpenseFilter expenseFilter = filterFactory.createFilter(expenseType);
            List<Expense> filteredExpenses = expenseFilter.filter(expenseList);
            if (!filteredExpenses.isEmpty()) {
                expenseMonthSummarizeDTOList.add(monthSummarizeFactory(expenseList, filteredExpenses, expenseType));
            }
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
