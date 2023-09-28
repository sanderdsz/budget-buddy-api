package com.asana.budgetbuddy.expense.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ExpenseMonthlyDTO {

    private double value;
    private Integer month;

}
