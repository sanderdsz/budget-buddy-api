package com.asana.budgetbuddy.model;

import com.asana.budgetbuddy.enums.ExpenseType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "expenses")
public class Expense extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User user;

    @Column
    @NotNull
    private double value;

    @Column(name = "expense_type")
    @NotNull
    private ExpenseType expenseType;

    @Column(name = "date")
    @NotNull
    private LocalDate date;

    @Column(name = "description")
    @Size(max = 50)
    private String description;
}
