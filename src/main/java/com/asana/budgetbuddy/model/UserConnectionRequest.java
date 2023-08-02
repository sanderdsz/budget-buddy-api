package com.asana.budgetbuddy.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users_connection_requests")
public class UserConnectionRequest extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_parent")
    private User userParent;

    @OneToOne
    @JoinColumn(name = "user_children")
    private User userChildren;

    @Column(name = "is_email_verified")
    private Boolean isEmailVerified;
}
