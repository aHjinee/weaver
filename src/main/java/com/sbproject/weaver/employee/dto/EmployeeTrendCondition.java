package com.sbproject.weaver.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class EmployeeTrendCondition {

    private LocalDate from;
    private LocalDate to;
    private String unit; // day, week, month, quarter, year
}