package com.asana.budgetbuddy.expense.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseMonthSummarizeDTO {

    private List<ExpenseDTO> expenses;
    private String expenseType;
    private BigDecimal totalValue;
    private BigDecimal percentage;

}
