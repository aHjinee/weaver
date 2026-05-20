package com.sbproject.weaver.employee.dto;

import com.sbproject.weaver.employee.entity.EmployeeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeSearchDistribution {
    private String groupBy;
    private EmployeeStatus status;
}
