package com.asana.budgetbuddy.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users_data")
public class UserData extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User userId;

    @Column(name = "password")
    private String password;

    @Column(name = "refresh_token")
    private String refreshToken;
}
