package com.asana.budgetbuddy.controller;

import com.asana.budgetbuddy.dto.UserDTO;
import com.asana.budgetbuddy.dto.UserMapper;
import com.asana.budgetbuddy.model.User;
import com.asana.budgetbuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
        Optional<User> user = userService.getById(id);
        if (user.isPresent()) {
            UserDTO userDTO = UserMapper.toDTO(user.get());
            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User save(@RequestBody User user) {
        userService.save(user);
        return user;
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> put(
            @RequestBody User user,
            @PathVariable Long id
    ) {
        Optional<User> currentUser = userService.getById(id);
        if (currentUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            currentUser.get().setEmail(user.getEmail());
            currentUser.get().setUserParent(user.getUserParent());
            currentUser.get().setUserChildren(user.getUserChildren());
            userService.save(currentUser.get());
            UserDTO userDTO = UserMapper.toDTO(currentUser.get());
            return ResponseEntity.ok(userDTO);
        }
    }

}
