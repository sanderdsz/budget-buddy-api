package com.asana.budgetbuddy.importFile.dto;

import lombok.*;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ImportFileDTO {

    private double value;
    private String entry;
    private String type;
    private Date date;
    private String description;
}
