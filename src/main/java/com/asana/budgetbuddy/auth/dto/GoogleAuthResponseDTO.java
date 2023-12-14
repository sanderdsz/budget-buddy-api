package com.asana.budgetbuddy.auth.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoogleAuthResponseDTO {

    private String azp;
    private String aud;
    private String sub;
    private String scope;
    private String exp;
    private String expires_in;
    private String email;
    private String email_verified;
    private String access_type;

}
