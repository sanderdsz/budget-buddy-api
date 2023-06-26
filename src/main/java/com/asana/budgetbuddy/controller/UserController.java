package com.asana.budgetbuddy.controller;

import com.asana.budgetbuddy.dto.user.UserDTO;
import com.asana.budgetbuddy.dto.user.UserMapper;
import com.asana.budgetbuddy.dto.user.UserUpdateDTO;
import com.asana.budgetbuddy.model.User;
import com.asana.budgetbuddy.model.UserData;
import com.asana.budgetbuddy.service.UserDataService;
import com.asana.budgetbuddy.service.UserService;
import com.asana.budgetbuddy.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<User> getByUserId(@RequestHeader("Authorization") String accessToken) {
        Optional<String> parsedToken = jwtUtil.parseAccessToken(accessToken);
        String userId = jwtUtil.getUserIdFromAccessToken(parsedToken.get());
        Optional<User> user = userService.getById(Long.parseLong(userId));
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        Optional<User> user = userService.getById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getByEmail(@PathVariable String email) {
        Optional<User> user = userService.getByEmail(email);
        if (user.isPresent()) {
            Optional<UserData> userData = userDataService.getByUserId(user.get().getId());
            UserDTO userDTO = UserMapper.toDTO(user.get(), userData.get());
            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> put(
            @RequestBody UserUpdateDTO userUpdate,
            @PathVariable Long id
    ) {
        Optional<User> currentUser = userService.getById(id);
        if (currentUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            currentUser.get().setUserParent(userUpdate.getUserParent());
            currentUser.get().setUserChildren(userUpdate.getUserChildren());
            userService.update(currentUser.get());
            UserData userData = userDataService.update(currentUser.get(), userUpdate.getPassword());
            UserDTO userDTO = UserMapper.toDTO(currentUser.get(), userData);
            return ResponseEntity.ok(userDTO);
        }
    }
}
