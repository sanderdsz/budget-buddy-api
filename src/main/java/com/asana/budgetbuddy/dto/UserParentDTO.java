package com.asana.budgetbuddy.dto;

import jakarta.persistence.Id;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserParentDTO {

    @Id
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

}
