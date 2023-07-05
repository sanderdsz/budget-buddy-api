package com.asana.budgetbuddy.dto.expense;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseDTO {

    private double value;
    private String expenseType;
    private LocalDate date;
    private String description;

}
