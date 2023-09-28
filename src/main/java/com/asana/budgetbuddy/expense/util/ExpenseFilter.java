package com.asana.budgetbuddy.expense.util;

import com.asana.budgetbuddy.expense.model.Expense;

import java.util.List;

public interface ExpenseFilter {
    List<Expense> filter(List<Expense> expenses);
}
