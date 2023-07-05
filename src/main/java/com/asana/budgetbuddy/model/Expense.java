package com.asana.budgetbuddy.model;

import com.asana.budgetbuddy.enums.ExpenseType;
import jakarta.persistence.*;
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
    private User user;

    @Column
    private double value;

    @Column(name = "expense_type")
    private ExpenseType expenseType;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "description")
    private String description;
}
