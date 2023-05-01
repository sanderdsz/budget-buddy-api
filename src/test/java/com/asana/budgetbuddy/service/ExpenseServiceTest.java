package com.asana.budgetbuddy.service;

import com.asana.budgetbuddy.enums.ExpenseType;
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

    @Autowired
    protected UserService userService;

    @Test
    void shouldGetById() {
        User newUser = User.builder()
                .name("Lorem Ipsum")
                .email("lorem@ipsum.com")
                .build();
        User user = this.userService.save(newUser);

        Expense newExpense = Expense.builder()
                .value(10.00)
                .expenseType(ExpenseType.GROCERY)
                .user(user)
                .build();
        Expense expense = this.expenseService.save(newExpense);
        Optional<Expense> oldExpense = this.expenseService.getById(expense.getId());
        assertThat(oldExpense.get().getId()).isNotEqualTo(0L);
    }

    @Test
    void shouldSave() {
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
