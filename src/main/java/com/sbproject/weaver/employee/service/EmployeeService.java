package com.sbproject.weaver.employee.service;

import com.sbproject.weaver.common.dto.CursorPageResponse;
import com.sbproject.weaver.employee.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {

    EmployeeDto create(EmployeeCreateRequest request, MultipartFile profile);

    EmployeeDto findById(UUID id);

    EmployeeDto update(UUID id, EmployeeUpdateRequest request, MultipartFile profile);

    void delete(UUID id);

    CursorPageResponse<EmployeeDto> findAll(EmployeeSearchCondition condition);

    List<EmployeeTrendDto> getTrend(EmployeeTrendCondition condition);

    Long count(EmployeeCountCondition condition);

    List<EmployeeDistributionDto> getDistribution(EmployeeSearchDistribution searchDistribution);
}
