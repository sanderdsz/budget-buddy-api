package com.asana.budgetbuddy.controller;

import com.asana.budgetbuddy.dto.BalanceDTO;
import com.asana.budgetbuddy.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/balances")
public class BalanceController {

    @Autowired
    private BalanceService balanceService;

    @GetMapping("/user")
    public ResponseEntity<BalanceDTO> getBalanceByUserEmail(@RequestParam String email) {
        BalanceDTO balanceDTO = balanceService.getBalanceByEmail(email);
        return ResponseEntity.ok(balanceDTO);
    }

}
