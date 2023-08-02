package com.asana.budgetbuddy.dto.user;

import com.asana.budgetbuddy.model.User;
import com.asana.budgetbuddy.model.UserConnectionRequest;
import com.asana.budgetbuddy.model.UserData;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is a mapper for the DTO return of User class,
 * made to avoid infinite loop from the entity JSON return.
 */
public class UserMapper {

    public static Collection<UserChildrenDTO> toChildrenDTO(User user) {
        Collection<User> users = user.getUserChildren();
        Collection<UserChildrenDTO> userChildrenDTOS =
                users.stream().map(userObj -> new UserChildrenDTO(
                        userObj.getId(),
                        userObj.getFirstName(),
                        userObj.getLastName(),
                        userObj.getEmail())
                ).toList();
        return userChildrenDTOS;
    }

    public static UserParentDTO toParentDTO(User user) {
        UserParentDTO userParentDTO = new UserParentDTO(
                user.getUserParent().getId(),
                user.getUserParent().getFirstName(),
                user.getUserParent().getLastName(),
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
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .userParent(toParentDTO(user))
                    .accessToken(userData.getAccessToken())
                    .build();
        } else if (user.getUserChildren() != null) {
            userDTO = UserDTO
                    .builder()
                    .id(user.getId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .userChildren(toChildrenDTO(user))
                    .accessToken(userData.getAccessToken())
                    .build();
        } else {
            userDTO = UserDTO
                    .builder()
                    .id(user.getId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
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
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .userChildren(toChildrenDTO(user))
                .build();
    }

    public static Collection<UserConnectionRequestDTO> toUserConnectionChildrenDTO(List<UserConnectionRequest> userConnectionRequestList) {
        Collection<UserConnectionRequestDTO> userConnectionRequestDTOS = userConnectionRequestList.stream()
                .map(userConnectionRequest -> new UserConnectionRequestDTO(
                        userConnectionRequest.getUserChildren().getId(),
                        userConnectionRequest.getUserChildren().getFirstName(),
                        userConnectionRequest.getUserChildren().getLastName(),
                        userConnectionRequest.getUserChildren().getEmail(),
                        userConnectionRequest.getIsEmailVerified()
                )).toList();
        return userConnectionRequestDTOS;
    }

    public static Collection<UserConnectionDTO> toUserConnectionByChildrenDTO(
            List<UserConnectionRequest> userConnectionRequestList
    ) {
        Collection<UserConnectionDTO> userConnectionDTOS = userConnectionRequestList.stream()
                .map(userConnectionRequest -> new UserConnectionDTO(
                        userConnectionRequest.getUserParent().getId(),
                        userConnectionRequest.getUserParent().getFirstName(),
                        userConnectionRequest.getUserParent().getLastName(),
                        userConnectionRequest.getUserParent().getEmail(),
                        userConnectionRequest.getIsEmailVerified(),
                        true
                )).toList();
        return userConnectionDTOS;
    }

    public static Collection<UserConnectionDTO> toUserConnectionByParentDTO(
            List<UserConnectionRequest> userConnectionRequestList
    ) {
        Collection<UserConnectionDTO> userConnectionDTOS = userConnectionRequestList.stream()
                .map(userConnectionRequest -> new UserConnectionDTO(
                        userConnectionRequest.getUserChildren().getId(),
                        userConnectionRequest.getUserChildren().getFirstName(),
                        userConnectionRequest.getUserChildren().getLastName(),
                        userConnectionRequest.getUserChildren().getEmail(),
                        userConnectionRequest.getIsEmailVerified(),
                        false
                )).toList();
        return userConnectionDTOS;
    }

    public static List<UserConnectionDTO> toUserConnectionByUserDTO(
            User user
    ) {
        List<UserConnectionDTO> userConnectionDTOS = user.getUserChildren().stream()
                .map(userChildren -> new UserConnectionDTO(
                        userChildren.getId(),
                        userChildren.getFirstName(),
                        userChildren.getLastName(),
                        userChildren.getEmail(),
                        true,
                        false
                )).collect(Collectors.toList());
        if (user.getUserParent() != null) {
            UserConnectionDTO userConnectionParentDTO = UserConnectionDTO
                    .builder()
                    .id(user.getUserParent().getId())
                    .firstName(user.getUserParent().getFirstName())
                    .lastName(user.getUserParent().getLastName())
                    .email(user.getUserParent().getEmail())
                    .isParent(true)
                    .isEmailVerified(true)
                    .build();
            userConnectionDTOS.add(userConnectionParentDTO);
        }
        return userConnectionDTOS;
    }
}

