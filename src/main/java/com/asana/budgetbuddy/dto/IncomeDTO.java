package com.asana.budgetbuddy.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncomeDTO {

    private double value;
    private String expenseType;

}
