package com.asana.budgetbuddy.util;

import com.asana.budgetbuddy.enums.ExpenseType;
import com.asana.budgetbuddy.model.Expense;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExpenseFilterFactory {

    public ExpenseFilter createFilter(ExpenseType type) {
        switch (type) {
            case HOUSING:
                return new HousingExpenseFilter();
            case GROCERY:
                return new GroceryExpenseFilter();
            case MEALS:
                return new MealsExpenseFilter();
            case CAR:
                return new CarExpenseFilter();
            case PHARMACY:
                return new PharmacyExpenseFilter();
            case SHOPPING:
                return new ShoppingExpenseFilter();
            case TRAVELS:
                return new TravelsExpenseFilter();
            default:
                throw new IllegalArgumentException("Unsupported expense type: " + type);
        }
    }

    static class HousingExpenseFilter implements ExpenseFilter {
        @Override
        public List<Expense> filter(List<Expense> expenses) {
            return expenses.stream()
                    .filter(expense -> expense.getExpenseType() == ExpenseType.HOUSING)
                    .collect(Collectors.toList());
        }
    }

    static class GroceryExpenseFilter implements ExpenseFilter {
        @Override
        public List<Expense> filter(List<Expense> expenses) {
            return expenses.stream()
                    .filter(expense -> expense.getExpenseType() == ExpenseType.GROCERY)
                    .collect(Collectors.toList());
        }
    }

    static class MealsExpenseFilter implements ExpenseFilter {
        @Override
        public List<Expense> filter(List<Expense> expenses) {
            return expenses.stream()
                    .filter(expense -> expense.getExpenseType() == ExpenseType.MEALS)
                    .collect(Collectors.toList());
        }
    }

    static class CarExpenseFilter implements ExpenseFilter {
        @Override
        public List<Expense> filter(List<Expense> expenses) {
            return expenses.stream()
                    .filter(expense -> expense.getExpenseType() == ExpenseType.CAR)
                    .collect(Collectors.toList());
        }
    }

    static class ShoppingExpenseFilter implements ExpenseFilter {
        @Override
        public List<Expense> filter(List<Expense> expenses) {
            return expenses.stream()
                    .filter(expense -> expense.getExpenseType() == ExpenseType.SHOPPING)
                    .collect(Collectors.toList());
        }
    }

    static class PharmacyExpenseFilter implements ExpenseFilter {
        @Override
        public List<Expense> filter(List<Expense> expenses) {
            return expenses.stream()
                    .filter(expense -> expense.getExpenseType() == ExpenseType.PHARMACY)
                    .collect(Collectors.toList());
        }
    }

    static class TravelsExpenseFilter implements ExpenseFilter {
        @Override
        public List<Expense> filter(List<Expense> expenses) {
            return expenses.stream()
                    .filter(expense -> expense.getExpenseType() == ExpenseType.TRAVELS)
                    .collect(Collectors.toList());
        }
    }
}
