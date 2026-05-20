package com.sbproject.weaver.employee.service;

import com.sbproject.weaver.common.dto.CursorPageResponse;
import com.sbproject.weaver.employee.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public interface EmployeeService {

    CursorPageResponse<EmployeeDto> findAll(EmployeeSearchCondition condition);

    List<EmployeeTrendDto> getTrend(EmployeeTrendCondition condition);

    Long count(EmployeeCountCondition condition);

    List<EmployeeDistributionDto> getDistribution(EmployeeSearchDistribution searchDistribution);
}