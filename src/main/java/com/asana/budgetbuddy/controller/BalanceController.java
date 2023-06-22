package com.asana.budgetbuddy.controller;

import com.asana.budgetbuddy.dto.BalanceDTO;
import com.asana.budgetbuddy.service.BalanceService;
import com.asana.budgetbuddy.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;


@RestController
@RequestMapping("/balances")
public class BalanceController {

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping()
    public ResponseEntity<BalanceDTO> getBalanceByUserIdAndDateBetween(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        Optional<String> parsedToken = jwtUtil.parseAccessToken(accessToken);
        String userId = jwtUtil.getUserIdFromAccessToken(parsedToken.get());
        if (year != null & month != null) {
            BalanceDTO balanceDTO = balanceService.getBalanceByUserIdAndYearAndMonth(Long.parseLong(userId), year, month);
            return ResponseEntity.ok(balanceDTO);
        }
        if (startDate != null & endDate != null) {
            LocalDate parsedStartDate = LocalDate.parse(startDate);
            LocalDate parsedEndDate = LocalDate.parse(endDate);
            BalanceDTO balanceDTO = balanceService.getBalanceByUserIdAndDateBetween(Long.parseLong(userId), parsedStartDate, parsedEndDate);
            return ResponseEntity.ok(balanceDTO);
        }
        return ResponseEntity.notFound().build();
    }
}
