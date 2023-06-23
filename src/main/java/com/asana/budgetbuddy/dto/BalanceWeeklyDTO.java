package com.asana.budgetbuddy.dto;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BalanceWeeklyDTO {

    private BalanceDTO weekBalance;
    private Integer week;
    private LocalDate startDate;
    private LocalDate endDate;

}
