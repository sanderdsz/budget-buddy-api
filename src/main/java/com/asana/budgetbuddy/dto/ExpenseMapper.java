package com.asana.budgetbuddy.dto;

import com.asana.budgetbuddy.model.Expense;

import java.util.List;

public class ExpenseMapper {

    public static List<ExpenseDTO> toDTO(List<Expense> expenses) {
        List<ExpenseDTO> expenseDTOS = expenses.stream()
                .map(expense -> new ExpenseDTO(
                        expense.getValue(),
                        expense.getExpenseType().toString()))
                .toList();
        return expenseDTOS;
    }
}
