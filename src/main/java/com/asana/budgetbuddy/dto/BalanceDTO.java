package com.asana.budgetbuddy.dto;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceDTO {

    private BigDecimal value;

}
