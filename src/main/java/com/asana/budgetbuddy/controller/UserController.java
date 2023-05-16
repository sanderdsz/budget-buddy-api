package com.asana.budgetbuddy.controller;

import com.asana.budgetbuddy.dto.UserDTO;
import com.asana.budgetbuddy.dto.UserMapper;
import com.asana.budgetbuddy.dto.UserRegistrationDTO;
import com.asana.budgetbuddy.dto.UserUpdateDTO;
import com.asana.budgetbuddy.model.User;
import com.asana.budgetbuddy.model.UserData;
import com.asana.budgetbuddy.service.UserDataService;
import com.asana.budgetbuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDataService userDataService;

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        Optional<User> user = userService.getById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserDTO> save(@RequestBody UserRegistrationDTO userRegistration)
            throws InvalidAlgorithmParameterException,
            NoSuchPaddingException,
            IllegalBlockSizeException,
            NoSuchAlgorithmException,
            InvalidKeySpecException,
            BadPaddingException,
            InvalidKeyException {
        User user = userService.save(userRegistration);
        UserData userData = userDataService.save(userRegistration, user);
        UserDTO userDTO = UserMapper.toDTO(user, userData);
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> put(
            @RequestBody UserUpdateDTO userUpdate,
            @PathVariable Long id
    ) throws InvalidAlgorithmParameterException,
            NoSuchPaddingException,
            IllegalBlockSizeException,
            NoSuchAlgorithmException,
            InvalidKeySpecException,
            BadPaddingException,
            InvalidKeyException {
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
