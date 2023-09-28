package com.asana.budgetbuddy.income.controller;

import com.asana.budgetbuddy.income.dto.IncomeDTO;
import com.asana.budgetbuddy.income.dto.IncomeMapper;
import com.asana.budgetbuddy.income.enums.IncomeType;
import com.asana.budgetbuddy.income.model.Income;
import com.asana.budgetbuddy.user.model.User;
import com.asana.budgetbuddy.income.service.IncomeService;
import com.asana.budgetbuddy.user.service.UserService;
import com.asana.budgetbuddy.shared.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/incomes")
public class IncomeController {

    @Autowired
    private IncomeService incomeService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity save(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody IncomeDTO incomeDTO
    ) {
        Optional<String> parsedToken = jwtUtil.parseAccessToken(accessToken);
        String userId = jwtUtil.getUserIdFromAccessToken(parsedToken.get());
        Optional<User> user = userService.getById(Long.valueOf(userId));
        Income income = IncomeMapper.toModel(incomeDTO, user.get());
        incomeService.save(income);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncomeDTO> getById(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable Long id
    ) {
        Optional<String> parsedToken = jwtUtil.parseAccessToken(accessToken);
        String userId = jwtUtil.getUserIdFromAccessToken(parsedToken.get());
        if (userId != null) {
            Income income = incomeService.getById(id);
            IncomeDTO incomeDTO = IncomeMapper.toDTOSingle(income);
            return ResponseEntity.ok(incomeDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/pageable")
    public ResponseEntity<List<IncomeDTO>> getAllPageable(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String incomeType,
            @RequestParam(required = false) String date
    ) {
        Optional<String> parsedToken = jwtUtil.parseAccessToken(accessToken);
        String userId = jwtUtil.getUserIdFromAccessToken(parsedToken.get());
        if (userId != null) {
            PageRequest pageable = PageRequest.of(page, size);
            List<Income> incomes;
            if (date != null & incomeType != null) {
                incomes = incomeService.getAllByUserIdAndDateAndIncomeTypePageable(
                        Long.parseLong(userId),
                        LocalDate.parse(date),
                        IncomeType.valueOf(incomeType),
                        pageable
                );
            } else if (date != null) {
                incomes = incomeService.getAllByUserIdAndDatePageable(
                        Long.parseLong(userId),
                        LocalDate.parse(date),
                        pageable
                );
            } else if (incomeType != null) {
                incomes = incomeService.getAllByUserIdAndIncomeTypePageable(
                        Long.parseLong(userId),
                        IncomeType.valueOf(incomeType),
                        pageable
                );
            } else {
                incomes = incomeService.getAllByUserIdPageable(Long.parseLong(userId), pageable);
            }
            List<IncomeDTO> incomeDTOS = IncomeMapper.toDTO(incomes);
            return ResponseEntity.ok(incomeDTOS);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping()
    public ResponseEntity<List<IncomeDTO>> getIncomeByUserIdAndDate(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        Optional<String> parsedToken = jwtUtil.parseAccessToken(accessToken);
        String userId = jwtUtil.getUserIdFromAccessToken(parsedToken.get());
        if (year != null & month != null) {
            List<Income> incomes = incomeService.getAllByUserEmailAndYearAndMonth(Long.parseLong(userId), year, month);
            List<IncomeDTO> incomeDTOS = IncomeMapper.toDTO(incomes);
            return ResponseEntity.ok(incomeDTOS);
        }
        if (startDate != null & endDate != null) {
            LocalDate parsedStartDate = LocalDate.parse(startDate);
            LocalDate parsedEndDate = LocalDate.parse(endDate);
            List<Income> incomes = incomeService.getAllByUserEmailAndDateBetween(Long.parseLong(userId), parsedStartDate, parsedEndDate);
            List<IncomeDTO> incomeDTOS = IncomeMapper.toDTO(incomes);
            return ResponseEntity.ok(incomeDTOS);
        }
        return ResponseEntity.notFound().build();
    }
}
