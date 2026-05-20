package com.sbproject.weaver.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCreateRequest {

    private String name;
    private String email;
    private UUID departmentId;
    private String position;
    private LocalDate hireDate;
    private String memo;
}