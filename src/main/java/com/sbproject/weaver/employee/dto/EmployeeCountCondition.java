package com.sbproject.weaver.employee.dto;

import com.sbproject.weaver.employee.entity.EmployeeStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class EmployeeCountCondition {

    private EmployeeStatus status;
    private LocalDate from;
    private LocalDate to;
}