package com.sbproject.weaver.employee.repository;

import com.sbproject.weaver.common.dto.CursorPageResponse;
import com.sbproject.weaver.employee.dto.*;

import java.util.List;

public interface EmployeeRepositoryCustom {

    CursorPageResponse<EmployeeDto> search(EmployeeSearchCondition condition);

    List<EmployeeTrendDto> getTrend(EmployeeTrendCondition condition);

    List<EmployeeDistributionDto> distribution(EmployeeSearchDistribution searchDistribution);

    Long countEmployees(EmployeeCountCondition condition);
}