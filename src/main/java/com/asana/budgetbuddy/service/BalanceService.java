package com.asana.budgetbuddy.service;

import com.asana.budgetbuddy.dto.BalanceDTO;
import com.asana.budgetbuddy.dto.BalanceWeeklyDTO;
import com.asana.budgetbuddy.model.Expense;
import com.asana.budgetbuddy.model.Income;
import com.asana.budgetbuddy.repository.ExpenseRepository;
import com.asana.budgetbuddy.repository.IncomeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
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
        List <BalanceWeeklyDTO> balanceWeekly = new ArrayList<>();
        List<List<LocalDate>> weeksOfMonth = new ArrayList<>();
        List<LocalDate> week = new ArrayList<>();
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        LocalDate lastDayOfMonth = LocalDate.of(year, month, firstDayOfMonth.lengthOfMonth());
        /* the first date which initialize all the month days */
        LocalDate date = firstDayOfMonth;
        int weekNumber = 0;
        while (!date.isAfter(lastDayOfMonth)) {
            week.add(date);
            /* check if the day is the last week day to wrap up the week into array */
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
