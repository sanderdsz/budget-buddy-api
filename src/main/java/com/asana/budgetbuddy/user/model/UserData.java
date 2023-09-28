package com.asana.budgetbuddy.user.model;

import com.asana.budgetbuddy.model.BaseEntity;
import com.asana.budgetbuddy.user.model.User;
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
    private User user;

    @Column(name = "password")
    private String password;

    @Transient
    private String accessToken;
}
