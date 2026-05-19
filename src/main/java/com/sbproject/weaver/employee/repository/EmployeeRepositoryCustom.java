package com.sbproject.weaver.employee.repository;

import com.sbproject.weaver.common.dto.CursorPageResponse;
import com.sbproject.weaver.employee.dto.EmployeeDto;
import com.sbproject.weaver.employee.dto.EmployeeSearchCondition;

public interface EmployeeRepositoryCustom {

    CursorPageResponse<EmployeeDto> search(EmployeeSearchCondition condition);
}