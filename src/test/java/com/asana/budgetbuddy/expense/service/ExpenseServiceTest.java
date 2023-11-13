package com.asana.budgetbuddy.expense.service;

import com.asana.budgetbuddy.auth.service.AuthService;
import com.asana.budgetbuddy.expense.enums.ExpenseType;
import com.asana.budgetbuddy.expense.model.Expense;
import com.asana.budgetbuddy.user.model.User;
import com.asana.budgetbuddy.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ExpenseServiceTest {

    @Autowired
    protected ExpenseService expenseService;
    @Autowired
    protected UserService userService;
    @Autowired
    protected AuthService authService;
    private Expense expense;

    @BeforeEach
    public void setUp() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Lorem");
        user.setLastName("Ipsum");
        user.setEmail("lorem@ipsum.com");

        expense = new Expense();
        expense.setUser(user);
        expense.setValue(10.00);
        expense.setExpenseType(ExpenseType.CAR);
        expense.setDate(LocalDate.now());
        expense.setDescription("Lorem Ipsum");
    }

    @Test
    void shouldGetById() {
        this.expenseService.save(expense);
        Optional<Expense> oldExpense = Optional.ofNullable(this.expenseService.getById(expense.getId()));
        assertThat(oldExpense.get().getId()).isNotEqualTo(0L);
    }

    @Test
    void shouldSave() {
        this.expenseService.save(expense);
        assertThat(expense.getId().longValue()).isNotEqualTo(0L);
    }

}
