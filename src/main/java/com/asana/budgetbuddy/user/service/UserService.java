package com.asana.budgetbuddy.user.service;

import com.asana.budgetbuddy.user.dto.UserConnectionDTO;
import com.asana.budgetbuddy.user.dto.UserMapper;
import com.asana.budgetbuddy.user.model.User;
import com.asana.budgetbuddy.user.model.UserConnectionRequest;
import com.asana.budgetbuddy.user.repository.UserConnectionRequestRepository;
import com.asana.budgetbuddy.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserConnectionRequestRepository userConnectionRequestRepository;

    @Transactional
    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public Collection<UserConnectionDTO> getAllConnections(Long id) {
        Collection<UserConnectionDTO> userConnections = new ArrayList<>();
        User user = userRepository.findById(id).get();
        Collection<UserConnectionDTO> userConnectionByUserDTOS = UserMapper.toUserConnectionByUserDTO(user);
        List<UserConnectionRequest> userConnectionRequestsByParent = userConnectionRequestRepository.findAllByUserParent_IdOrderByCreatedAtDesc(id);
        Collection<UserConnectionDTO> userConnectionByParentDTOS = UserMapper.toUserConnectionByParentDTO(
                userConnectionRequestsByParent
        );
        List<UserConnectionRequest> userConnectionRequestsByChildren = userConnectionRequestRepository.findAllByUserChildren_IdOrderByCreatedAtDesc(id);
        Collection<UserConnectionDTO> userConnectionByChildrenDTOS = UserMapper.toUserConnectionByChildrenDTO(
                userConnectionRequestsByChildren
        );
        userConnections.addAll(userConnectionByUserDTOS);
        userConnections.addAll(userConnectionByChildrenDTOS);
        userConnections.addAll(userConnectionByParentDTOS);
        return userConnections;
    }

    @Transactional
    public User update(User user) {
        userRepository.save(user);
        return user;
    }

    @Transactional
    public User uploadAvatar(Long id, MultipartFile file) throws IOException {
        byte[] avatar = file.getBytes();
        Optional<User> user = userRepository.findById(id);
        user.get().setAvatar(avatar);
        userRepository.save(user.get());
        return user.get();
    }
}
