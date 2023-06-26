package com.asana.budgetbuddy.util;

import com.asana.budgetbuddy.model.Expense;

import java.util.List;

public interface ExpenseFilter {
    List<Expense> filter(List<Expense> expenses);
}
