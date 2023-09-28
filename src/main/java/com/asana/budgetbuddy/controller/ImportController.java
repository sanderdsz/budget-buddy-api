package com.asana.budgetbuddy.controller;

import com.asana.budgetbuddy.dto.importFile.ImportFileDTO;
import com.asana.budgetbuddy.enums.ExpenseType;
import com.asana.budgetbuddy.enums.IncomeType;
import com.asana.budgetbuddy.model.Expense;
import com.asana.budgetbuddy.model.Income;
import com.asana.budgetbuddy.user.model.User;
import com.asana.budgetbuddy.service.ExpenseService;
import com.asana.budgetbuddy.service.IncomeService;
import com.asana.budgetbuddy.user.service.UserService;
import com.asana.budgetbuddy.util.JwtUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@RestController
@RequestMapping("/import")
public class ImportController {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private IncomeService incomeService;

    @Autowired
    private ExpenseService expenseService;

    @PostMapping()
    public ResponseEntity uploadFile(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        Optional<String> parsedToken = jwtUtil.parseAccessToken(accessToken);
        String userId = jwtUtil.getUserIdFromAccessToken(parsedToken.get());
        Optional<User> user = userService.getById(Long.parseLong(userId));
        if (hasExcelFormat(file)) {
            xlsxMapper(file.getInputStream(), user.get());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    public static boolean hasExcelFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    public void xlsxMapper(InputStream inputStream, User user) {
        try {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            List<ImportFileDTO> importList = new ArrayList<>();
            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }
                ImportFileDTO importFile = new ImportFileDTO();
                Iterator<Cell> cellsInRow = currentRow.iterator();
                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();
                    switch (cellIdx) {
                        case 0 -> importFile.setValue(currentCell.getNumericCellValue());
                        case 1 -> importFile.setEntry(currentCell.getStringCellValue());
                        case 2 -> importFile.setType(currentCell.getStringCellValue());
                        case 3 -> importFile.setDate(currentCell.getDateCellValue());
                        case 4 -> importFile.setDescription(currentCell.getStringCellValue());
                        default -> {
                        }
                    }
                    cellIdx++;
                }
                if (importFile.getValue() != 0.0) {
                    importList.add(importFile);
                }
            }
            importList.forEach(importFileDTO -> {
                LocalDate dateConversion = convertDateToLocalDate(importFileDTO.getDate());
                if (importFileDTO.getEntry().equalsIgnoreCase("expense")) {
                    Expense expense = Expense.builder()
                            .user(user)
                            .value(importFileDTO.getValue())
                            .expenseType(ExpenseType.valueOf(importFileDTO.getType().toUpperCase()))
                            .date(dateConversion)
                            .description(importFileDTO.getDescription())
                            .build();
                    expenseService.save(expense);
                }
                if (importFileDTO.getEntry().equalsIgnoreCase("income")) {
                    Income income = Income.builder()
                            .user(user)
                            .value(importFileDTO.getValue())
                            .incomeType(IncomeType.valueOf(importFileDTO.getType().toUpperCase()))
                            .date(dateConversion)
                            .description(importFileDTO.getDescription())
                            .build();
                    incomeService.save(income);
                }
            });
        } catch (IOException ex) {
            throw new RuntimeException("fail to parse file: " + ex.getMessage());
        }
    }

    public static LocalDate convertDateToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}



