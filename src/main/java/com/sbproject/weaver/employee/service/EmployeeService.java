package com.sbproject.weaver.employee.service;

import com.sbproject.weaver.common.dto.CursorPageResponse;
import com.sbproject.weaver.employee.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {

    EmployeeDto create(EmployeeCreateRequest request, MultipartFile profile, HttpServletRequest httpRequest);

    EmployeeDto findById(UUID id);

    EmployeeDto update(UUID id, EmployeeUpdateRequest request, MultipartFile profile, HttpServletRequest httpRequest);

    void delete(UUID id, HttpServletRequest httpRequest);

    CursorPageResponse<EmployeeDto> findAll(EmployeeSearchCondition condition);

    List<EmployeeTrendDto> getTrend(EmployeeTrendCondition condition);

    Long count(EmployeeCountCondition condition);

    List<EmployeeDistributionDto> getDistribution(EmployeeSearchDistribution searchDistribution);
}
