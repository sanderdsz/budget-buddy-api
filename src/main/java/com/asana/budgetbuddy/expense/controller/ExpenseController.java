package com.asana.budgetbuddy.expense.controller;

import com.asana.budgetbuddy.expense.dto.*;
import com.asana.budgetbuddy.expense.enums.ExpenseType;
import com.asana.budgetbuddy.shared.exception.UnauthorizedException;
import com.asana.budgetbuddy.expense.model.Expense;
import com.asana.budgetbuddy.user.model.User;
import com.asana.budgetbuddy.expense.service.ExpenseService;
import com.asana.budgetbuddy.user.service.UserService;
import com.asana.budgetbuddy.shared.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/expenses")
@Validated
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity save(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody ExpenseDTO expenseDTO
    ) {
        Optional<String> parsedToken = jwtUtil.parseAccessToken(accessToken);
        String userId = jwtUtil.getUserIdFromAccessToken(parsedToken.get());
        Optional<User> user = userService.getById(Long.parseLong(userId));
        Expense expense = ExpenseMapper.toModel(expenseDTO, user.get());
        expenseService.save(expense);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity delete(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable Long id
    ) {
        Optional<String> parsedToken = jwtUtil.parseAccessToken(accessToken);
        String userId = jwtUtil.getUserIdFromAccessToken(parsedToken.get());
        Expense expense = expenseService.getById(id);
        if (Long.parseLong(userId) != expense.getUser().getId()) {
            throw new UnauthorizedException();
        }
        expenseService.delete(expense.getId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity put(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody ExpenseDTO expenseDTO
    ) {
        Optional<String> parsedToken = jwtUtil.parseAccessToken(accessToken);
        String userId = jwtUtil.getUserIdFromAccessToken(parsedToken.get());
        Optional<User> user = userService.getById(Long.parseLong(userId));
        expenseService.put(expenseDTO, user.get());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable Long id
    ) {
        Optional<String> parsedToken = jwtUtil.parseAccessToken(accessToken);
        String userId = jwtUtil.getUserIdFromAccessToken(parsedToken.get());
        Expense expense = expenseService.getById(id);
        if (Long.parseLong(userId) != expense.getUser().getId()) {
            throw new UnauthorizedException();
        }
        ExpenseDTO dto = ExpenseMapper.toDTOSingle(expense);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/pageable")
    public ResponseEntity<List<ExpenseDTO>> getAllPageable(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String expenseType,
            @RequestParam(required = false) String date
    ) {
        Optional<String> parsedToken = jwtUtil.parseAccessToken(accessToken);
        String userId = jwtUtil.getUserIdFromAccessToken(parsedToken.get());
        if (userId != null) {
            PageRequest pageable = PageRequest.of(page, size);
            List<Expense> expenses;
            if (date != null & expenseType != null) {
                expenses = expenseService.getAllByUserIdAndDateAndExpenseTypePageable(
                        Long.parseLong(userId),
                        LocalDate.parse(date),
                        ExpenseType.valueOf(expenseType),
                        pageable
                );
            } else if (expenseType != null) {
                expenses = expenseService.getAllByUserIdAndExpenseTypePageable(
                        Long.parseLong(userId),
                        ExpenseType.valueOf(expenseType),
                        pageable
                );
            } else if (date != null) {
                expenses = expenseService.getAllByUserIdAndDatePageable(
                        Long.parseLong(userId),
                        LocalDate.parse(date),
                        pageable
                );
            } else {
                expenses = expenseService.getAllByUserIdPageable(Long.parseLong(userId), pageable);
            }
            List<ExpenseDTO> expenseDTOS = ExpenseMapper.toDTO(expenses);
            return ResponseEntity.ok(expenseDTOS);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/summary/month/{month}")
    public ResponseEntity<List<ExpenseMonthSummarizeDTO>> getMonthlySummarizedByTypeAndValue(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable Integer month
    ) {
        Optional<String> parsedToken = jwtUtil.parseAccessToken(accessToken);
        String userId = jwtUtil.getUserIdFromAccessToken(parsedToken.get());
        if (userId != null) {
            List<ExpenseMonthSummarizeDTO> summarizeDTOS = expenseService.getMonthlySummarizedByTypeAndValue(
                    Long.valueOf(userId),
                    month
            );
            summarizeDTOS.sort(Comparator.comparing(ExpenseMonthSummarizeDTO::getTotalValue).reversed());
            return ResponseEntity.ok(summarizeDTOS);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping()
    public ResponseEntity<List<ExpenseDTO>> getExpenseByUserIdAndDateBetween(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        Optional<String> parsedToken = jwtUtil.parseAccessToken(accessToken);
        String userId = jwtUtil.getUserIdFromAccessToken(parsedToken.get());
        if (year != null & month != null) {
            List<Expense> expenses = expenseService.getAllByUserEmailAndYearAndMonth(Long.parseLong(userId), year, month);
            List<ExpenseDTO> expenseDTOS = ExpenseMapper.toDTO(expenses);
            return ResponseEntity.ok(expenseDTOS);
        }
        if (startDate != null & endDate != null) {
            LocalDate parsedStartDate = LocalDate.parse(startDate);
            LocalDate parsedEndDate = LocalDate.parse(endDate);
            List<Expense> expenses = expenseService.getAllByUserEmailAndDateBetween(Long.parseLong(userId), parsedStartDate, parsedEndDate);
            List<ExpenseDTO> expenseDTOS = ExpenseMapper.toDTO(expenses);
            return ResponseEntity.ok(expenseDTOS);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/connected")
    public ResponseEntity<List<ExpenseConnectedDTO>> getAllUsersChildrenExpenses(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String expenseType,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Long childrenId
    ) {
        List<Long> ids = new ArrayList<>();
        PageRequest pageable = PageRequest.of(page, size);
        ExpenseType expenseTypeParsed = null;
        LocalDate dateParsed = null;
        Optional<String> parsedToken = jwtUtil.parseAccessToken(accessToken);
        String userId = jwtUtil.getUserIdFromAccessToken(parsedToken.get());
        Optional<User> user = userService.getById(Long.parseLong(userId));
        user.get().getUserChildren().forEach(userChildren -> ids.add(userChildren.getId()));
        if (date != null & expenseType != null) {
            expenseTypeParsed = ExpenseType.valueOf(expenseType);
            dateParsed = LocalDate.parse(date);
        } else if (expenseType != null) {
            expenseTypeParsed = ExpenseType.valueOf(expenseType);
        } else if (date != null) {
            dateParsed = LocalDate.parse(date);
        }
        if (childrenId != null) {
            ids.clear();
            ids.add(childrenId);
        }
        if (user.get().getUserParent() != null) {
            ids.add(user.get().getUserParent().getId());
        }
        List<Expense> expenses = new ArrayList<>();
        expenses = expenseService.getAllUsersChildrenExpenses(ids, dateParsed, expenseTypeParsed, pageable);
        List<ExpenseConnectedDTO> expenseDTOS = ExpenseMapper.toDTOConnected(expenses);
        return ResponseEntity.ok(expenseDTOS);
    }

    @GetMapping("/total/month")
    public ResponseEntity<ExpenseTotalDTO> getTotalByMonth(
            @RequestHeader("Authorization") String accessToken
    ) {
        Optional<String> parsedToken = jwtUtil.parseAccessToken(accessToken);
        String userId = jwtUtil.getUserIdFromAccessToken(parsedToken.get());
        if (userId != null) {
            LocalDate startDate = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1);
            LocalDate endDate = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), startDate.lengthOfMonth());
            List<Expense> expenseList = expenseService.getAllByUserEmailAndDateBetween(
                    Long.valueOf(userId),
                    startDate,
                    endDate
            );
            double sum = expenseList.stream()
                    .mapToDouble(Expense::getValue)
                    .sum();
            ExpenseTotalDTO expenseTotalDTO = ExpenseTotalDTO
                    .builder()
                    .value(sum)
                    .build();
            return ResponseEntity.ok(expenseTotalDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/total/year")
    public ResponseEntity<ExpenseTotalDTO> getTotalByYear(
            @RequestHeader("Authorization") String accessToken
    ) {
        Optional<String> parsedToken = jwtUtil.parseAccessToken(accessToken);
        String userId = jwtUtil.getUserIdFromAccessToken(parsedToken.get());
        if (userId != null) {
            LocalDate startDate = LocalDate.of(LocalDate.now().getYear(), 1, 1);
            LocalDate endDate = LocalDate.of(LocalDate.now().getYear(), 12, 31);
            List<Expense> expenseList = expenseService.getAllByUserEmailAndDateBetween(
                    Long.valueOf(userId),
                    startDate,
                    endDate
            );
            double sum = expenseList.stream()
                    .mapToDouble(Expense::getValue)
                    .sum();
            ExpenseTotalDTO expenseTotalDTO = ExpenseTotalDTO
                    .builder()
                    .value(sum)
                    .build();
            return ResponseEntity.ok(expenseTotalDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/year")
    public ResponseEntity<List<ExpenseMonthlyDTO>> getMonthExpensesByYear(
            @RequestHeader("Authorization") String accessToken
    ) {
        Optional<String> parsedToken = jwtUtil.parseAccessToken(accessToken);
        String userId = jwtUtil.getUserIdFromAccessToken(parsedToken.get());
        if (userId != null) {
            List<ExpenseMonthlyDTO> expenseMonthly = expenseService.getYearExpenses(Long.parseLong(userId));
            return ResponseEntity.ok(expenseMonthly);
        }
        return ResponseEntity.notFound().build();
    }

}
