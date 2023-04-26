package com.asana.budgetbuddy.controller;

import com.asana.budgetbuddy.model.Expense;
import com.asana.budgetbuddy.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Expense save(@RequestBody Expense expense) {
        expenseService.save(expense);
        return expense;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Expense> getById(@PathVariable Long id) {
        Optional<Expense> expense = expenseService.getById(id);
        if (expense.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(expense.get());
        }
    }
}
