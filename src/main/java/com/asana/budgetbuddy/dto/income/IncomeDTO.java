package com.asana.budgetbuddy.dto.income;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncomeDTO {

    private Long id;
    private double value;
    private String incomeType;
    private LocalDate date;
    private String description;

}
