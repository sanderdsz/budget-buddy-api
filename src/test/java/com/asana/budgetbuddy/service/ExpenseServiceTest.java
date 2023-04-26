package com.asana.budgetbuddy.service;

import com.asana.budgetbuddy.model.Expense;
import com.asana.budgetbuddy.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ExpenseServiceTest {

    @Autowired
    protected ExpenseService expenseService;

    @Test
    void shouldFindExpenseById() {
        Optional<Expense> expense = this.expenseService.getById(1L);
        assertThat(expense.isPresent()).isTrue();
    }

    @Test
    void shouldInsertExpense() {
        User user = new User();
        user.setId(1L);
        user.setName("Lorem Ipsum");
        user.setEmail("lorem@ipsum.com");

        Expense expense = new Expense();
        expense.setUser(user);
        expense.setValue(10.00);
        this.expenseService.save(expense);
        assertThat(expense.getId().longValue()).isNotEqualTo(0L);
    }

}
