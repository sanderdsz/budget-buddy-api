package com.asana.budgetbuddy.user.model;

import com.asana.budgetbuddy.shared.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "users_data_external")
public class UserDataExternal extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "provider_name")
    private String providerName;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "provider_refresh_token")
    private String providerToken;

}
