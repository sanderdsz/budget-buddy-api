package com.asana.budgetbuddy.balance.service;

import com.asana.budgetbuddy.balance.dto.BalanceDTO;
import com.asana.budgetbuddy.balance.dto.BalanceMonthlyDTO;
import com.asana.budgetbuddy.balance.dto.BalanceWeeklyDTO;
import com.asana.budgetbuddy.expense.model.Expense;
import com.asana.budgetbuddy.income.model.Income;
import com.asana.budgetbuddy.expense.repository.ExpenseRepository;
import com.asana.budgetbuddy.income.repository.IncomeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
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
        return calculateBalance(id, startDate, endDate);
    }

    @Transactional
    public BalanceDTO getBalanceByUserIdAndDateBetween(Long id, LocalDate startDate, LocalDate endDate) {
        return calculateBalance(id, startDate, endDate);
    }

    @Transactional
    public List<BalanceWeeklyDTO> getWeeklyBalanceByUserId(Long id) {
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();
        List<BalanceWeeklyDTO> balanceWeekly = new ArrayList<>();
        List<List<LocalDate>> weeksOfMonth = new ArrayList<>();
        List<LocalDate> week = new ArrayList<>();
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        LocalDate lastDayOfMonth = LocalDate.of(year, month, firstDayOfMonth.lengthOfMonth());
        LocalDate date = firstDayOfMonth;
        int weekNumber = 0;
        while (!date.isAfter(lastDayOfMonth)) {
            week.add(date);
            if (date.getDayOfWeek() == DayOfWeek.SUNDAY || date.equals(lastDayOfMonth)) {
                weeksOfMonth.add(week);
                week = new ArrayList<>();
            }
            date = date.plusDays(1);
        }
        for (List<LocalDate> weekDates : weeksOfMonth) {
            weekNumber++;
            LocalDate stardDate = weekDates.get(0);
            LocalDate endDate = weekDates.get(weekDates.size() - 1);
            BalanceDTO balance = calculateBalance(id, stardDate, endDate);
            BalanceWeeklyDTO balanceWeeklyDTO = BalanceWeeklyDTO
                    .builder()
                    .weekBalance(balance)
                    .week(weekNumber)
                    .startDate(stardDate)
                    .endDate(endDate)
                    .build();
            balanceWeekly.add(balanceWeeklyDTO);
        }
        return balanceWeekly;
    }

    @Transactional
    public List<BalanceMonthlyDTO> getMonthlyBalanceByUserId(Long id) {
        List<BalanceMonthlyDTO> balanceMonthly = new ArrayList<>();
        int year = LocalDate.now().getYear();
        for (Month month : Month.values()) {
            LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
            LocalDate lastDayOfMonth = LocalDate.of(year, month, firstDayOfMonth.lengthOfMonth());
            BalanceDTO balance = calculateBalance(id, firstDayOfMonth, lastDayOfMonth);
            BalanceMonthlyDTO monthlyDTO = BalanceMonthlyDTO
                    .builder()
                    .monthBalance(balance)
                    .month(month.getValue())
                    .startDate(firstDayOfMonth)
                    .endDate(lastDayOfMonth)
                    .build();
            balanceMonthly.add(monthlyDTO);
        }
        return balanceMonthly;
    }

    private BalanceDTO calculateBalance(Long id, LocalDate startDate, LocalDate endDate) {
        List<Income> incomes = incomeRepository.findAllByUser_IdAndDateBetweenOrderByDateDesc(id, startDate, endDate);
        List<Expense> expenses = expenseRepository.findAllByUser_IdAndDateBetweenOrderByDateDesc(id, startDate, endDate);
        double incomeSum = incomes.stream().mapToDouble(Income::getValue).sum();
        double expenseSum = expenses.stream().mapToDouble(Expense::getValue).sum();
        return new BalanceDTO(scaleValue(incomeSum - expenseSum), scaleValue(expenseSum), scaleValue(incomeSum));
    }

    private BigDecimal scaleValue(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }
}
