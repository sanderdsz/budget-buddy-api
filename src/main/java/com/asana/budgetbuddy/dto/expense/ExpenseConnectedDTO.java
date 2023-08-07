package com.asana.budgetbuddy.dto.expense;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseConnectedDTO {
    private Long id;
    private double value;
    private String expenseType;
    private LocalDate date;
    private String description;
    private String userName;
}
