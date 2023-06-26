package com.asana.budgetbuddy.dto.user;

import jakarta.persistence.Id;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserChildrenDTO {

    @Id
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

}
