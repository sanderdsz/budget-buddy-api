package com.asana.budgetbuddy.dto;

import com.asana.budgetbuddy.model.User;
import com.asana.budgetbuddy.model.UserData;

import java.util.Collection;

/*
 * This class is a mapper for the DTO return of User class,
 * made to avoid infinite loop from the entity JSON return.
 */

public class UserMapper {

    public static Collection<UserChildrenDTO> toChildrenDTO(User user) {
        Collection<User> users = user.getUserChildren();
        Collection<UserChildrenDTO> userChildrenDTOS =
                users.stream().map(userObj -> new UserChildrenDTO(
                        userObj.getId(),
                        userObj.getName(),
                        userObj.getEmail())
                ).toList();
        return userChildrenDTOS;
    }

    public static UserParentDTO toParentDTO(User user) {
        UserParentDTO userParentDTO = new UserParentDTO(
                user.getUserParent().getId(),
                user.getUserParent().getName(),
                user.getUserParent().getEmail()
        );
        return userParentDTO;
    }

    public static UserDTO toDTO(User user, UserData userData) {
        UserDTO userDTO;
        if (user.getUserParent() != null) {
            userDTO = UserDTO
                    .builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .userParent(toParentDTO(user))
                    .accessToken(userData.getAccessToken())
                    .build();
        } else if (user.getUserChildren() != null) {
            userDTO = UserDTO
                    .builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .userChildren(toChildrenDTO(user))
                    .accessToken(userData.getAccessToken())
                    .build();
        } else {
            userDTO = UserDTO
                    .builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .accessToken(userData.getAccessToken())
                    .build();
        }
        return userDTO;
    }

    public static UserDTO toDTOWithoutAccess(User user) {
        return UserDTO
                    .builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .userChildren(toChildrenDTO(user))
                    .build();
    }
}

