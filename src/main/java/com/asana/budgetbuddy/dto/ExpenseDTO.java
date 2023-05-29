package com.asana.budgetbuddy.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseDTO {

    private double value;
    private String expenseType;

}
