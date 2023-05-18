package com.asana.budgetbuddy.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginDTO {

    private String email;
    private String password;
}
