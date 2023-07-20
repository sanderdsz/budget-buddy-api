package com.asana.budgetbuddy.dto.income;

import com.asana.budgetbuddy.enums.IncomeType;
import com.asana.budgetbuddy.model.Income;
import com.asana.budgetbuddy.model.User;

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

    public static Income toModel(IncomeDTO incomeDTO, User user) {
        Income income = Income.builder()
                .user(user)
                .value(incomeDTO.getValue())
                .incomeType(IncomeType.valueOf(incomeDTO.getIncomeType()))
                .date(incomeDTO.getDate())
                .build();
        return income;
    }
}
