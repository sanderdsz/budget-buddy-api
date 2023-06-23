package com.asana.budgetbuddy.dto;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BalanceDTO {

    private BigDecimal balance;
    private BigDecimal expenses;
    private BigDecimal incomes;

}
