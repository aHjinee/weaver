package com.sbproject.weaver.employee.service;

import com.sbproject.weaver.common.dto.CursorPageResponse;
import com.sbproject.weaver.employee.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public interface EmployeeService {

    EmployeeDto create(EmployeeCreateRequest request, MultipartFile profile);

    EmployeeDto findById(UUID id);

    CursorPageResponse<EmployeeDto> findAll(EmployeeSearchCondition condition);

    EmployeeDto update(UUID id, EmployeeUpdateRequest request, MultipartFile profile);

    void delete(UUID id);

    List<EmployeeTrendDto> getTrend(EmployeeTrendCondition condition);

    Long count(EmployeeCountCondition condition);
}