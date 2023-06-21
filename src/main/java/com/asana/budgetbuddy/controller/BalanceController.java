package com.asana.budgetbuddy.controller;

import com.asana.budgetbuddy.dto.BalanceDTO;
import com.asana.budgetbuddy.service.BalanceService;
import com.asana.budgetbuddy.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/balances")
public class BalanceController {

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping()
    public ResponseEntity<BalanceDTO> getBalanceByUserId(@RequestHeader("Authorization") String accessToken) {
        Optional<String> parsedToken = jwtUtil.parseAccessToken(accessToken);
        String userId = jwtUtil.getUserIdFromAccessToken(parsedToken.get());
        BalanceDTO balanceDTO = balanceService.getBalanceByUserId(Long.parseLong(userId));
        return ResponseEntity.ok(balanceDTO);
    }

    @GetMapping("/user")
    public ResponseEntity<BalanceDTO> getBalanceByUserEmail(@RequestParam String email) {
        BalanceDTO balanceDTO = balanceService.getBalanceByEmail(email);
        return ResponseEntity.ok(balanceDTO);
    }
}
