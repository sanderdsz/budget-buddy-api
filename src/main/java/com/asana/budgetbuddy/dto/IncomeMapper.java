package com.asana.budgetbuddy.dto;

import com.asana.budgetbuddy.model.Expense;
import com.asana.budgetbuddy.model.Income;

import java.util.List;

public class IncomeMapper {

    public static List<IncomeDTO> toDTO(List<Income> incomes) {
        List<IncomeDTO> incomeDTOS = incomes.stream()
                .map(income -> new IncomeDTO(
                        income.getValue(),
                        income.getIncomeType().toString(),
                        income.getDate()))
                .toList();
        return incomeDTOS;
    }
}
