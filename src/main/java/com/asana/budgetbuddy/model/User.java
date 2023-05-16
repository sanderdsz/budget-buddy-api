package com.asana.budgetbuddy.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Nullable
    @OneToMany
    @JoinTable(
            name = "users_children",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_children", referencedColumnName = "id")
    )
    private Collection<User> userChildren;

    @Nullable
    @ManyToOne
    @JoinTable(
            name = "users_parent",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_parent", referencedColumnName = "id")
    )
    private User userParent;

    @Column(name = "is_external")
    private boolean isExternal = false;
}
