package com.asana.budgetbuddy.expense.service;

import com.asana.budgetbuddy.expense.dto.ExpenseDTO;
import com.asana.budgetbuddy.expense.dto.ExpenseMonthSummarizeDTO;
import com.asana.budgetbuddy.expense.dto.ExpenseMonthlyDTO;
import com.asana.budgetbuddy.expense.enums.ExpenseType;
import com.asana.budgetbuddy.expense.model.Expense;
import com.asana.budgetbuddy.expense.repository.ExpenseRepository;
import com.asana.budgetbuddy.expense.util.ExpenseFilter;
import com.asana.budgetbuddy.expense.util.ExpenseFilterFactory;
import com.asana.budgetbuddy.shared.exception.EntityNotFoundException;
import com.asana.budgetbuddy.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ExpenseServiceTest {

    @InjectMocks
    protected ExpenseService expenseService;

    @Mock
    protected ExpenseRepository expenseRepository;

    @Mock
    protected ExpenseFilterFactory filterFactory;

    @Mock
    protected ExpenseFilter expenseFilter;

    private Expense expense;

    private User user;

    private ExpenseDTO expenseDTO;

    @BeforeEach
    public void setUp() {
        user = User
                .builder()
                .id(1L)
                .firstName("Lorem")
                .lastName("Ipsum")
                .email("lorem@ipsum.com")
                .build();

        expense = Expense
                .builder()
                .id(1L)
                .user(user)
                .value(10.00)
                .expenseType(ExpenseType.CAR)
                .date(LocalDate.now())
                .description("Lorem Ipsum")
                .build();

        expenseDTO = ExpenseDTO
                .builder()
                .id(1L)
                .value(10.00)
                .expenseType(ExpenseType.CAR.toString())
                .date(LocalDate.now())
                .description("Lorem Ipsum")
                .build();
    }

    @Test
    void givenValidId_whenGetById_thenReturnGetById() {
        // Mock
        when(expenseRepository.findById(expense.getId())).thenReturn(Optional.of(expense));
        // Act
        Optional<Expense> resultExpense = Optional.ofNullable(this.expenseService.getById(expense.getId()));
        // Assert
        assertThat(resultExpense.get()).isEqualTo(expense);
    }

    @Test
    public void givenInvalidId_whenGetById_thenReturnNotFoundException() {
        // Mock
        when(expenseRepository.findById(anyLong())).thenReturn(Optional.empty());
        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> {
            expenseService.getById(expense.getId());
        });

    }

    @Test
    void givenValidExpense_whenSave_thenSave() {
        // Act
        this.expenseService.save(expense);
        // Assert
        assertThat(expense.getId().longValue()).isNotEqualTo(0L);
    }

    @Test
    void givenValidID_whenDelete_thenDelete() {
        // Act
        this.expenseService.delete(expense.getId());
        // Assert
        Mockito.verify(expenseRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void givenValidExpenseAndUser_whenPut_thenPut() {
        // Mock
        when(expenseRepository.findById(expenseDTO.getId())).thenReturn(Optional.of(expense));
        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0));
        // Act
        Expense resultExpense = expenseService.put(expenseDTO, user);
        // Assert
        assertThat(resultExpense).isNotNull();
        assertThat(resultExpense.getId()).isEqualTo(expense.getId());
    }

    @Test
    void givenInvalidExpense_whenPut_thenReturnEntityNotFoundException() {
        // Mock
        when(expenseRepository.findById(anyLong())).thenReturn(Optional.empty());
        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> {
            expenseService.put(expenseDTO, user);
        });
    }

    @Test
    void givenUserIDAndMonth_whenGetMonthlySummarizedByTypeAndValue_thenReturnExpenseMonthSummarizeDTO() {
        // Mock
        List<Expense> expenseList = new ArrayList<>();
        expenseList.add(expense);
        when(expenseRepository.findAllByUser_IdAndDateBetweenOrderByDateDesc(
                user.getId(),
                LocalDate.of(2023, 10, 1),
                LocalDate.of(2023, 10, 31))
        ).thenReturn(expenseList);
        when(filterFactory.createFilter(any())).thenReturn(expenseFilter);
        when(expenseFilter.filter(any())).thenReturn(expenseList);
        // Act
        List<ExpenseMonthSummarizeDTO> result = expenseService.getMonthlySummarizedByTypeAndValue(
                user.getId(),
                10
        );
        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    void givenIDsDateExpenseTypeAndPageable_whenGetAllUsersChildrenExpenses_thenReturnListExpenses() {
        // Arrange
        List<Long> ids = Arrays.asList(1L, 2L);
        LocalDate date = LocalDate.of(2023, 10, 15);
        ExpenseType expenseType = ExpenseType.MEALS;
        Pageable pageable = Mockito.mock(Pageable.class);
        // Mock
        List<Expense> expensesAllFilters = Arrays.asList(new Expense(), new Expense());
        when(expenseRepository.findAllByUser_IdAndDateAndExpenseTypeOrderByDateDesc(
                1L,
                date,
                expenseType,
                pageable)
        ).thenReturn(expensesAllFilters);
        List<Expense> expensesWithDate = Arrays.asList(new Expense(), new Expense());
        when(expenseRepository.findAllByUser_IdAndDateOrderByDateDesc(
                1L,
                date,
                pageable)
        ).thenReturn(expensesWithDate);
        List<Expense> expensesWithType = Arrays.asList(new Expense(), new Expense());
        when(expenseRepository.findAllByUser_IdAndExpenseTypeOrderByDateDesc(
                1L,
                expenseType,
                pageable)
        ).thenReturn(expensesWithType);
        List<Expense> expensesWithoutDateAndType = Arrays.asList(new Expense(), new Expense());
        when(expenseRepository.findAllByUser_IdOrderByDateDesc(
                1L,
                pageable)
        ).thenReturn(expensesWithoutDateAndType);
        // Act
        List<Expense> resultAllFilters = expenseService.getAllUsersChildrenExpenses(ids, date, expenseType, pageable);
        List<Expense> resultWithDate = expenseService.getAllUsersChildrenExpenses(ids, null, expenseType, pageable);
        List<Expense> resultWithType = expenseService.getAllUsersChildrenExpenses(ids, date, null, pageable);
        List<Expense> resultWithoutDateAndType = expenseService.getAllUsersChildrenExpenses(ids, null, null, pageable);
        // Assert
        assertThat(resultAllFilters).isNotNull();
        assertThat(resultAllFilters).hasSize(2);
        assertThat(resultWithDate).isNotNull();
        assertThat(resultWithType).isNotNull();
        assertThat(resultWithoutDateAndType).isNotNull();
    }

    @Test
    void givenUserIdAndDates_whenGetYearExpenses_thenReturnListExpensesMonthlyDTO() {
        // Mocking expenses for each month
        for (Month month : Month.values()) {
            LocalDate firstDayOfMonth = LocalDate.of(2023, month, 1);
            LocalDate lastDayOfMonth = LocalDate.of(2023, month, firstDayOfMonth.lengthOfMonth());
            List<Expense> monthExpenses = new ArrayList<>();
            when(expenseRepository.findAllByUser_IdAndDateBetweenOrderByDateDesc(
                    user.getId(),
                    firstDayOfMonth,
                    lastDayOfMonth)
            ).thenReturn(monthExpenses);
        }
        // Act
        List<ExpenseMonthlyDTO> result = expenseService.getYearExpenses(user.getId());
        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(12);
    }

    @Test
    void givenIDAndDates_whenGetAllByUserEmailAndDateBetween_thenReturnListExpenses() {
        // Mock
        List<Expense> expenses = Arrays.asList(new Expense(), new Expense());
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 28);
        when(expenseRepository.findAllByUser_IdAndDateBetweenOrderByDateDesc(
                1L,
                startDate,
                endDate
        )).thenReturn(expenses);
        // Act
        List<Expense> result = expenseService.getAllByUserEmailAndDateBetween(
                1L,
                startDate,
                endDate
        );
        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    void givenIdYearAndMonth_whenGetAllByUserEmailAndYearAndMonth_thenReturnListExpenses() {
        // Mock
        List<Expense> expenses = Arrays.asList(new Expense(), new Expense());
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, startDate.lengthOfMonth());
        when(expenseRepository.findAllByUser_IdAndDateBetweenOrderByDateDesc(
                1L,
                startDate,
                endDate
        )).thenReturn(expenses);
        // Act
        List<Expense> result = expenseService.getAllByUserEmailAndYearAndMonth(
                1L,
                1,
                1
        );
        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    void givenIdAndPageable_whenGetAllByUserIdPageable_thenReturnListExpenses() {
        // Mock
        List<Expense> expenses = Arrays.asList(new Expense(), new Expense());
        Pageable pageable = Mockito.mock(Pageable.class);
        when(expenseRepository.findAllByUser_IdOrderByDateDesc(
                user.getId(),
                pageable
        )).thenReturn(expenses);
        // Act
        List<Expense> result = expenseService.getAllByUserIdPageable(
                user.getId(),
                pageable
        );
        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    void givenIdExpenseTypeAndPageable_whenGetAllByUserIdAndExpenseTypePageable_thenReturnListExpenses() {
        // Mock
        List<Expense> expenses = Arrays.asList(new Expense(), new Expense());
        Pageable pageable = Mockito.mock(Pageable.class);
        ExpenseType expenseType = ExpenseType.MEALS;
        when(expenseRepository.findAllByUser_IdAndExpenseTypeOrderByDateDesc(
                user.getId(),
                expenseType,
                pageable
        )).thenReturn(expenses);
        // Act
        List<Expense> result = expenseService.getAllByUserIdAndExpenseTypePageable(
                user.getId(),
                expenseType,
                pageable
        );
        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    void givenIdDateAndPageable_whenGetAllByUserIdAndDatePageable_thenReturnListExpenses() {
        // Mock
        List<Expense> expenses = Arrays.asList(new Expense(), new Expense());
        LocalDate date = LocalDate.of(2023, 1, 1);
        Pageable pageable = Mockito.mock(Pageable.class);
        when(expenseRepository.findAllByUser_IdAndDateOrderByDateDesc(
                user.getId(),
                date,
                pageable
        )).thenReturn(expenses);
        // Act
        List<Expense> result = expenseService.getAllByUserIdAndDatePageable(
                user.getId(),
                date,
                pageable
        );
        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    void givenIdDateExpenseTypeAndPageable_whenGetAllByUserIdAndDateAndExpenseTypePageable_thenReturnListExpenses() {
        // Mock
        List<Expense> expenses = Arrays.asList(new Expense(), new Expense());
        LocalDate date = LocalDate.of(2023, 1, 1);
        ExpenseType expenseType = ExpenseType.MEALS;
        Pageable pageable = Mockito.mock(Pageable.class);
        when(expenseRepository.findAllByUser_IdAndDateAndExpenseTypeOrderByDateDesc(
                user.getId(),
                date,
                expenseType,
                pageable
        )).thenReturn(expenses);
        // Act
        List<Expense> result = expenseService.getAllByUserIdAndDateAndExpenseTypePageable(
                user.getId(),
                date,
                expenseType,
                pageable
        );
        // Assert
        assertThat(result).isNotNull();
    }

}
