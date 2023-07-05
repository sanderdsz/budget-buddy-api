package com.asana.budgetbuddy.dto.expense;

import com.asana.budgetbuddy.enums.ExpenseType;
import com.asana.budgetbuddy.model.Expense;
import com.asana.budgetbuddy.model.User;

import java.util.List;

public class ExpenseMapper {

    public static List<ExpenseDTO> toDTO(List<Expense> expenses) {
        List<ExpenseDTO> expenseDTOS = expenses.stream()
                .map(expense -> new ExpenseDTO(
                        expense.getValue(),
                        expense.getExpenseType().toString(),
                        expense.getDate(),
                        expense.getDescription()))
                .toList();
        return expenseDTOS;
    }

    public static Expense toModel(ExpenseDTO expenseDTO, User user) {
        Expense expense = Expense.builder()
                .user(user)
                .value(expenseDTO.getValue())
                .expenseType(ExpenseType.valueOf(expenseDTO.getExpenseType()))
                .date(expenseDTO.getDate())
                .description(expenseDTO.getDescription())
                .build();
        return expense;
    }
}
