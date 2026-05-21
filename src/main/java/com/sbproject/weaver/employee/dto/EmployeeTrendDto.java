package com.sbproject.weaver.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmployeeTrendDto {

    private String date;
    private Long count;
    private Long change;
    private Double changeRate;
}