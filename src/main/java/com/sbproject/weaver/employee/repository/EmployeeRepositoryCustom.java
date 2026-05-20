package com.sbproject.weaver.employee.repository;

import com.sbproject.weaver.common.dto.CursorPageResponse;
import com.sbproject.weaver.employee.dto.EmployeeDistributionDto;
import com.sbproject.weaver.employee.dto.EmployeeDto;
import com.sbproject.weaver.employee.dto.EmployeeSearchCondition;
import com.sbproject.weaver.employee.dto.EmployeeSearchDistribution;

import java.util.List;

public interface EmployeeRepositoryCustom {

    CursorPageResponse<EmployeeDto> search(EmployeeSearchCondition condition);
    List<EmployeeDistributionDto> distribution(EmployeeSearchDistribution searchDistribution);
}