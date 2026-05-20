package com.sbproject.weaver.employee.dto;

import com.sbproject.weaver.employee.entity.EmployeeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.UUID;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeUpdateRequest {
    private String name;
    private String email;
    private UUID departmentId;
    private String position;
    private LocalDate hireDate;
    private EmployeeStatus status;
    private String memo;
}