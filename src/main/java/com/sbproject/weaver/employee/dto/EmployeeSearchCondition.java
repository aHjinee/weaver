package com.sbproject.weaver.employee.dto;

import com.sbproject.weaver.employee.entity.EmployeeStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class EmployeeSearchCondition {

    private String nameOrEmail;
    private String employeeNumber;
    private String departmentName;
    private String position;
    private LocalDate hireDateFrom;
    private LocalDate hireDateTo;
    private EmployeeStatus status;

    private String cursor;
    private Long idAfter;
    private Integer size;
    private String sortField;
    private String sortDirection;
}