package com.asana.budgetbuddy.model;

import com.asana.budgetbuddy.enums.IncomeType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "incomes")
public class Income extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "value")
    private double value;

    @Column(name = "income_type")
    private IncomeType incomeType;
}
