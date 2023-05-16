package com.asana.budgetbuddy.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationDTO {

    private String email;

    private String password;

    private String name;

    private boolean isExternal;

}
