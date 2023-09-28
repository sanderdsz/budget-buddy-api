package com.asana.budgetbuddy.balance.dto;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BalanceMonthlyDTO {

    private BalanceDTO monthBalance;
    private Integer month;
    private LocalDate startDate;
    private LocalDate endDate;

}
