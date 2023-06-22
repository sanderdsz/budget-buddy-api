package com.asana.budgetbuddy.controller;

import com.asana.budgetbuddy.dto.ExpenseDTO;
import com.asana.budgetbuddy.dto.ExpenseMapper;
import com.asana.budgetbuddy.model.Expense;
import com.asana.budgetbuddy.service.ExpenseService;
import com.asana.budgetbuddy.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity save(@RequestBody Expense expense) {
        expenseService.save(expense);
        return ResponseEntity.ok().build();
    }

    @GetMapping()
    public ResponseEntity<List<ExpenseDTO>> getExpenseByUserIdAndDateBetween(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        Optional<String> parsedToken = jwtUtil.parseAccessToken(accessToken);
        String userId = jwtUtil.getUserIdFromAccessToken(parsedToken.get());
        if (year != null & month != null) {
            List<Expense> expenses = expenseService.getAllByUserEmailAndYearAndMonth(Long.parseLong(userId), year, month);
            List<ExpenseDTO> expenseDTOS = ExpenseMapper.toDTO(expenses);
            return ResponseEntity.ok(expenseDTOS);
        }
        if (startDate != null & endDate != null) {
            LocalDate parsedStartDate = LocalDate.parse(startDate);
            LocalDate parsedEndDate = LocalDate.parse(endDate);
            List<Expense> expenses = expenseService.getAllByUserEmailAndDateBetween(Long.parseLong(userId), parsedStartDate, parsedEndDate);
            List<ExpenseDTO> expenseDTOS = ExpenseMapper.toDTO(expenses);
            return ResponseEntity.ok(expenseDTOS);
        }
        return ResponseEntity.notFound().build();
    }
}
