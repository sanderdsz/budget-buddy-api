package com.asana.budgetbuddy.expense.dto;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseDTO {

    private Long id;
    private double value;
    private String expenseType;
    private LocalDate date;
    private String description;
}
