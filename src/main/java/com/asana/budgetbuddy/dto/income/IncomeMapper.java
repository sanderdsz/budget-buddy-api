package com.asana.budgetbuddy.dto.income;

import com.asana.budgetbuddy.enums.IncomeType;
import com.asana.budgetbuddy.model.Income;
import com.asana.budgetbuddy.user.model.User;

import java.util.List;

public class IncomeMapper {

    public static IncomeDTO toDTOSingle(Income income) {
        IncomeDTO incomeDTO = IncomeDTO
                .builder()
                .id(income.getId())
                .incomeType(income.getIncomeType().toString())
                .date(income.getDate())
                .value(income.getValue())
                .description(income.getDescription())
                .build();
        return incomeDTO;
    }

    public static List<IncomeDTO> toDTO(List<Income> incomes) {
        List<IncomeDTO> incomeDTOS = incomes.stream()
                .map(income -> new IncomeDTO(
                        income.getId(),
                        income.getValue(),
                        income.getIncomeType().toString(),
                        income.getDate(),
                        income.getDescription()))
                .toList();
        return incomeDTOS;
    }

    public static Income toModel(IncomeDTO incomeDTO, User user) {
        Income income = Income.builder()
                .user(user)
                .value(incomeDTO.getValue())
                .incomeType(IncomeType.valueOf(incomeDTO.getIncomeType()))
                .date(incomeDTO.getDate())
                .description(incomeDTO.getDescription())
                .build();
        return income;
    }
}
