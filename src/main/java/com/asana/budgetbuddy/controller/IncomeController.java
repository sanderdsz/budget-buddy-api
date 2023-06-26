package com.asana.budgetbuddy.controller;

import com.asana.budgetbuddy.dto.income.IncomeDTO;
import com.asana.budgetbuddy.dto.income.IncomeMapper;
import com.asana.budgetbuddy.model.Income;
import com.asana.budgetbuddy.service.IncomeService;
import com.asana.budgetbuddy.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/incomes")
public class IncomeController {

    @Autowired
    private IncomeService incomeService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity save(@RequestBody Income income) {
        incomeService.save(income);
        return ResponseEntity.ok().build();
    }

    @GetMapping()
    public ResponseEntity<List<IncomeDTO>> getIncomeByUserIdAndDate(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        Optional<String> parsedToken = jwtUtil.parseAccessToken(accessToken);
        String userId = jwtUtil.getUserIdFromAccessToken(parsedToken.get());
        if (year != null & month != null) {
            List<Income> incomes = incomeService.getAllByUserEmailAndYearAndMonth(Long.parseLong(userId), year, month);
            List<IncomeDTO> incomeDTOS = IncomeMapper.toDTO(incomes);
            return ResponseEntity.ok(incomeDTOS);
        }
        if (startDate != null & endDate != null) {
            LocalDate parsedStartDate = LocalDate.parse(startDate);
            LocalDate parsedEndDate = LocalDate.parse(endDate);
            List<Income> incomes = incomeService.getAllByUserEmailAndDateBetween(Long.parseLong(userId), parsedStartDate, parsedEndDate);
            List<IncomeDTO> incomeDTOS = IncomeMapper.toDTO(incomes);
            return ResponseEntity.ok(incomeDTOS);
        }
        return ResponseEntity.notFound().build();
    }
}
