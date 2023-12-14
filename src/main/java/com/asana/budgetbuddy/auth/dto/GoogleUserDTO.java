package com.asana.budgetbuddy.auth.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoogleUserDTO {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String image;

}
