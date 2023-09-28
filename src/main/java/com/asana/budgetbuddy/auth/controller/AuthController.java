package com.asana.budgetbuddy.auth.controller;

import com.asana.budgetbuddy.auth.dto.LoginDTO;
import com.asana.budgetbuddy.auth.dto.TokenDTO;
import com.asana.budgetbuddy.user.dto.UserRegistrationDTO;
import com.asana.budgetbuddy.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO loginDTO) {
        TokenDTO tokenDTO = authService.login(loginDTO);
        return ResponseEntity.ok(tokenDTO);
    }

    @PostMapping("/signup")
    public ResponseEntity<TokenDTO> save(@RequestBody UserRegistrationDTO userRegistrationDTO) {
        TokenDTO tokenDTO = authService.save(userRegistrationDTO);
        return ResponseEntity.ok(tokenDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity logout(@RequestBody TokenDTO tokenDTO) {
        authService.logout(tokenDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    public ResponseEntity<TokenDTO> verify(@RequestBody TokenDTO tokenDTO) {
        TokenDTO token = authService.verify(tokenDTO);
        return ResponseEntity.ok(token);
    }
}
