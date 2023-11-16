package com.asana.budgetbuddy.income.model;

import com.asana.budgetbuddy.income.enums.IncomeType;
import com.asana.budgetbuddy.shared.model.BaseEntity;
import com.asana.budgetbuddy.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "incomes")
public class Income extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "value")
    private double value;

    @Column(name = "income_type")
    private IncomeType incomeType;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "description")
    private String description;
}
