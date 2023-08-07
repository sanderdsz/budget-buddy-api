package com.asana.budgetbuddy.dto.expense;

import com.asana.budgetbuddy.enums.ExpenseType;
import com.asana.budgetbuddy.model.Expense;
import com.asana.budgetbuddy.model.User;

import java.util.List;

public class ExpenseMapper {

    public static List<ExpenseConnectedDTO> toDTOConnected(List<Expense> expenses) {
        List<ExpenseConnectedDTO> expenseDTOS = expenses.stream()
                .map(expense -> new ExpenseConnectedDTO(
                        expense.getId(),
                        expense.getValue(),
                        expense.getExpenseType().toString(),
                        expense.getDate(),
                        expense.getDescription(),
                        expense.getUser().getFirstName()))
                .toList();
        return expenseDTOS;
    }

    public static ExpenseDTO toDTOSingle(Expense expense) {
        ExpenseDTO expenseDTO = ExpenseDTO.builder()
                .id(expense.getId())
                .value(expense.getValue())
                .expenseType(expense.getExpenseType().toString())
                .date(expense.getDate())
                .description(expense.getDescription())
                .build();
        return expenseDTO;
    }

    public static List<ExpenseDTO> toDTO(List<Expense> expenses) {
        List<ExpenseDTO> expenseDTOS = expenses.stream()
                .map(expense -> new ExpenseDTO(
                        expense.getId(),
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
