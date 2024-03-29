package com.asana.budgetbuddy.user.dto;

import jakarta.persistence.Id;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserConnectionRequestDTO {

    @Id
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean isEmailVerified;
}
