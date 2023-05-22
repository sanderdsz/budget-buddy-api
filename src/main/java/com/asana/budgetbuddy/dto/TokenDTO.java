package com.asana.budgetbuddy.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenDTO {

    private String email;
    private String accessToken;
}
