package com.asana.budgetbuddy.expense.dto;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseTotalDTO {

    private double value;

}
