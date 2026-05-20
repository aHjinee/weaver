package com.sbproject.weaver.employee.service;

import com.sbproject.weaver.employee.dto.EmployeeDistributionDto;
import com.sbproject.weaver.employee.dto.EmployeeSearchDistribution;

import java.util.List;

public interface EmployeeService {
    List<EmployeeDistributionDto> getDistribution(EmployeeSearchDistribution searchDistribution);
}
