package com.asana.budgetbuddy.balance.controller;

import com.asana.budgetbuddy.balance.dto.BalanceDTO;
import com.asana.budgetbuddy.balance.dto.BalanceMonthlyDTO;
import com.asana.budgetbuddy.balance.dto.BalanceWeeklyDTO;
import com.asana.budgetbuddy.balance.service.BalanceService;
import com.asana.budgetbuddy.shared.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
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
        if (userId != null) {
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
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/weekly")
    public ResponseEntity<List<BalanceWeeklyDTO>> getWeeklyBalanceByUserId(@RequestHeader("Authorization") String accessToken) {
        Optional<String> parsedToken = jwtUtil.parseAccessToken(accessToken);
        String userId = jwtUtil.getUserIdFromAccessToken(parsedToken.get());
        if (userId != null) {
            List<BalanceWeeklyDTO> balanceWeekly = balanceService.getWeeklyBalanceByUserId(Long.parseLong(userId));
            return ResponseEntity.ok(balanceWeekly);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<BalanceMonthlyDTO>> getMonthlyBalanceByUserId(@RequestHeader("Authorization") String accessToken) {
        Optional<String> parsedToken = jwtUtil.parseAccessToken(accessToken);
        String userId = jwtUtil.getUserIdFromAccessToken(parsedToken.get());
        if (userId != null) {
            List<BalanceMonthlyDTO> balanceMonthly = balanceService.getMonthlyBalanceByUserId(Long.parseLong(userId));
            return ResponseEntity.ok(balanceMonthly);
        }
        return ResponseEntity.notFound().build();
    }
}
