package com.asana.budgetbuddy.controller;

import com.asana.budgetbuddy.dto.IncomeDTO;
import com.asana.budgetbuddy.dto.IncomeMapper;
import com.asana.budgetbuddy.model.Income;
import com.asana.budgetbuddy.service.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/incomes")
public class IncomeController {

    @Autowired
    private IncomeService incomeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity save(@RequestBody Income income) {
        incomeService.save(income);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user")
    public ResponseEntity<List<IncomeDTO>> getIncomeByUserEmail(@RequestParam String email) {
        List<Income> incomes = incomeService.getAllByUserEmail(email);
        List<IncomeDTO> incomeDTOS = IncomeMapper.toDTO(incomes);
        return ResponseEntity.ok(incomeDTOS);
    }
}
