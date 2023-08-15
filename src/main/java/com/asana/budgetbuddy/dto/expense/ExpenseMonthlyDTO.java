package com.asana.budgetbuddy.dto.expense;

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
