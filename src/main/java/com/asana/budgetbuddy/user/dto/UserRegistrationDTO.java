package com.asana.budgetbuddy.user.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationDTO {

    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private boolean isExternal;

}
