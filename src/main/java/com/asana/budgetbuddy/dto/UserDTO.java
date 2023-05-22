package com.asana.budgetbuddy.dto;

import jakarta.persistence.Id;
import lombok.*;

import java.util.Collection;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    @Id
    private Long id;

    private String name;

    private String email;

    private Collection<UserChildrenDTO> userChildren;

    private UserParentDTO userParent;

    private String accessToken;

}
