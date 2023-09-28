package com.asana.budgetbuddy.user.dto;

import com.asana.budgetbuddy.user.model.User;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Collection;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDTO {

    @Id
    private Long id;
    private String password;
    private Collection<User> userChildren;
    private User userParent;
}
