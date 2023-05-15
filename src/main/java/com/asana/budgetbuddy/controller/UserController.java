package com.asana.budgetbuddy.controller;

import com.asana.budgetbuddy.dto.UserDTO;
import com.asana.budgetbuddy.dto.UserMapper;
import com.asana.budgetbuddy.dto.UserRegistrationDTO;
import com.asana.budgetbuddy.model.User;
import com.asana.budgetbuddy.model.UserData;
import com.asana.budgetbuddy.service.UserDataService;
import com.asana.budgetbuddy.service.UserService;
import com.asana.budgetbuddy.util.AESEncryption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
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
    public UserRegistrationDTO save(@RequestBody UserRegistrationDTO userRegistration)
            throws NoSuchAlgorithmException,
            InvalidKeySpecException,
            InvalidAlgorithmParameterException,
            NoSuchPaddingException,
            IllegalBlockSizeException,
            BadPaddingException,
            InvalidKeyException
    {
        User newUser = User.builder()
                .name(userRegistration.getName())
                .email(userRegistration.getEmail())
                .build();
        User savedUser = userService.save(newUser);
        if (!userRegistration.isExternal()) {
            SecretKey key = AESEncryption.getKeyFromPassword(
                    userRegistration.getPassword(),
                    "salt"
            );
            IvParameterSpec iv = AESEncryption.generateIv();
            String encryptedPassword = AESEncryption.encryptPasswordBased(
                    "text",
                    key,
                    iv
            );
            UserData newUserData = UserData
                    .builder()
                    .userId(savedUser)
                    .password(encryptedPassword)
                    .build();
            userDataService.save(newUserData);
        }
        return userRegistration;
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
