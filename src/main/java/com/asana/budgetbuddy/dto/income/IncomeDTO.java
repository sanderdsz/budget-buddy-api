package com.asana.budgetbuddy.dto.income;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncomeDTO {

    private double value;
    private String expenseType;
    private LocalDate date;

}
