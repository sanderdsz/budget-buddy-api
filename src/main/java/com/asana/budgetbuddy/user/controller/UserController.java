package com.asana.budgetbuddy.user.controller;

import com.asana.budgetbuddy.user.exception.UserHasParentConnectionException;
import com.asana.budgetbuddy.user.dto.*;
import com.asana.budgetbuddy.user.model.User;
import com.asana.budgetbuddy.user.model.UserConnectionRequest;
import com.asana.budgetbuddy.user.model.UserData;
import com.asana.budgetbuddy.user.repository.UserConnectionRequestRepository;
import com.asana.budgetbuddy.shared.service.EmailService;
import com.asana.budgetbuddy.user.service.UserDataService;
import com.asana.budgetbuddy.user.service.UserService;
import com.asana.budgetbuddy.shared.util.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserConnectionRequestRepository userConnectionRequestRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<User> getByUserId(
            @RequestHeader("Authorization") String accessToken
    ) {
        Optional<String> parsedToken = jwtUtil.parseAccessToken(accessToken);
        String userId = jwtUtil.getUserIdFromAccessToken(parsedToken.get());
        Optional<User> user = userService.getById(Long.parseLong(userId));
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/email")
    public ResponseEntity<UserDTO> getByUserEmail(
            @RequestParam() String email
    ) {
        Optional<User> user = userService.getByEmail(email);
        if (user.isPresent()) {
            Optional<UserData> userData = userDataService.getByUserId(user.get().getId());
            UserDTO userDTO = UserMapper.toDTO(user.get(), userData.get());
            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getUserByUserId(
            @RequestHeader("Authorization") String accessToken
    ) {
        Optional<String> parsedToken = jwtUtil.parseAccessToken(accessToken);
        String userId = jwtUtil.getUserIdFromAccessToken(parsedToken.get());
        Optional<User> user = userService.getById(Long.valueOf(userId));
        if (user.isPresent()) {
            Optional<UserData> userData = userDataService.getByUserId(user.get().getId());
            UserDTO userDTO = UserMapper.toDTO(user.get(), userData.get());
            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping()
    public ResponseEntity<UserDTO> put(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody UserUpdateDTO userUpdate
    ) {
        Optional<String> parsedToken = jwtUtil.parseAccessToken(accessToken);
        String userId = jwtUtil.getUserIdFromAccessToken(parsedToken.get());
        Optional<User> currentUser = userService.getById(Long.parseLong(userId));
        if (currentUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            if (currentUser.get().getUserParent() != userUpdate.getUserParent() &
                    currentUser.get().getUserParent() != null
            ) {
                throw new UserHasParentConnectionException();
            }
            BeanUtils.copyProperties(userUpdate, currentUser.get(), "id");
            userService.update(currentUser.get());
            UserData userData = userDataService.update(currentUser.get(), userUpdate.getPassword());
            UserDTO userDTO = UserMapper.toDTO(currentUser.get(), userData);
            return ResponseEntity.ok(userDTO);
        }
    }

    @PostMapping("/avatar")
    public ResponseEntity<User> uploadAvatar(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        Optional<String> parsedToken = jwtUtil.parseAccessToken(accessToken);
        String userId = jwtUtil.getUserIdFromAccessToken(parsedToken.get());
        User user = userService.uploadAvatar(Long.valueOf(userId), file);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/avatar")
    public ResponseEntity<byte[]> getAvatar(@RequestHeader("Authorization") String accessToken) throws IOException {
        Optional<String> parsedToken = jwtUtil.parseAccessToken(accessToken);
        String userId = jwtUtil.getUserIdFromAccessToken(parsedToken.get());
        Optional<User> user = userService.getById(Long.valueOf(userId));
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        byte[] avatar = user.get().getAvatar();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<>(avatar, headers, HttpStatus.OK);
    }

    @PostMapping("/connections/new")
    public ResponseEntity<String> connectNewUser(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody UserConnectionEmailDTO userConnectionEmailDTO
    ) {
        Optional<String> parsedToken = jwtUtil.parseAccessToken(accessToken);
        String userId = jwtUtil.getUserIdFromAccessToken(parsedToken.get());
        Optional<User> user = userService.getById(Long.valueOf(userId));
        Optional<User> userConnection = userService.getByEmail(userConnectionEmailDTO.getEmail());
        if (userConnection.get().getUserParent() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This user has another network");
        }
        String message = emailService.sendConnectionRequest(
                user.get().getId(), userConnection.get().getId()
        );
        return ResponseEntity.ok(message);
    }

    @PostMapping("/connections/confirm/{id}")
    public ResponseEntity confirmConnection(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable Long id
    ) {
        Optional<String> parsedToken = jwtUtil.parseAccessToken(accessToken);
        String userId = jwtUtil.getUserIdFromAccessToken(parsedToken.get());
        Optional<User> user = userService.getById(Long.valueOf(userId));
        UserConnectionRequest request = userConnectionRequestRepository.findByUserChildren_IdOrUserParent_IdAndUserParent_IdOrUserChildren_Id(
                id,
                user.get().getId(),
                id,
                user.get().getId()
        ).get();
        Collection<User> usersList = new ArrayList<>();
        if (Objects.equals(user.get().getId(), request.getUserParent().getId())) {
            usersList.addAll(user.get().getUserChildren());
            usersList.add(request.getUserChildren());
            user.get().setUserChildren(usersList);
            userService.update(user.get());
            Optional<User> userChildren = userService.getById(id);
            userChildren.get().setUserParent(user.get());
            userService.update(userChildren.get());
            userConnectionRequestRepository.delete(request);
        } else {
            user.get().setUserParent(request.getUserParent());
            userService.update(user.get());
            Optional<User> userParent = userService.getById(id);
            usersList.addAll(userParent.get().getUserChildren());
            usersList.add(user.get());
            userParent.get().setUserChildren(usersList);
            userService.update(userParent.get());
            userConnectionRequestRepository.delete(request);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/connections/cancel/{id}")
    public ResponseEntity cancelConnection(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable Long id
    ) {
        Optional<String> parsedToken = jwtUtil.parseAccessToken(accessToken);
        String userId = jwtUtil.getUserIdFromAccessToken(parsedToken.get());
        Optional<User> user = userService.getById(Long.valueOf(userId));
        Optional<UserConnectionRequest> request = userConnectionRequestRepository.findByUserChildren_IdOrUserParent_IdAndUserParent_IdOrUserChildren_Id(
                id,
                user.get().getId(),
                id,
                user.get().getId()
        );
        List<User> userChildrenList = new ArrayList<>();
        // search the logged user children's for an ID match
        Optional<User> userChildrenFound = user.get().getUserChildren().stream().filter(userChildren -> userChildren.getId().equals(id)).findAny();
        if (userChildrenFound.isPresent()) {
            userChildrenList = user.get().getUserChildren().stream().filter(userChildren -> !userChildren.getId().equals(id)).toList();
            user.get().setUserChildren(userChildrenList);
            userService.update(user.get());
            userChildrenFound.get().setUserParent(null);
            userService.update(userChildrenFound.get());
        }
        if (user.get().getUserParent() != null) {
            user.get().setUserParent(null);
            userService.update(user.get());
            Optional<User> userParentFound = userService.getById(id);
            userParentFound.get().getUserChildren().removeIf(userRemove -> userRemove.getId().equals(user.get().getId()));
            userService.update(userParentFound.get());
        }
        if (request.isPresent()) {
            userConnectionRequestRepository.delete(request.get());
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/connections")
    public ResponseEntity<Collection<UserConnectionDTO>> getAllConnections(
            @RequestHeader("Authorization") String accessToken
    ) {
        Optional<String> parsedToken = jwtUtil.parseAccessToken(accessToken);
        String userId = jwtUtil.getUserIdFromAccessToken(parsedToken.get());
        Optional<User> user = userService.getById(Long.valueOf(userId));
        if (user.isPresent()) {
            Collection<UserConnectionDTO> userConnections = userService.getAllConnections(user.get().getId());
            return ResponseEntity.ok(userConnections);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
