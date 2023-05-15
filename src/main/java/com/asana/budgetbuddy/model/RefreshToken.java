package com.asana.budgetbuddy.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    private UUID id = UUID.randomUUID();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_data_id", referencedColumnName = "id")
    private UserData userDataId;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime expiresDate;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
}
