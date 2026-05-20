package com.sbproject.weaver.employee.service;

import com.sbproject.weaver.common.dto.CursorPageResponse;
import com.sbproject.weaver.department.entity.Department;
import com.sbproject.weaver.department.repository.DepartmentRepository;
import com.sbproject.weaver.employee.dto.*;
import com.sbproject.weaver.employee.entity.Employee;
import com.sbproject.weaver.employee.entity.EmployeeStatus;
import com.sbproject.weaver.employee.mapper.EmployeeMapper;
import com.sbproject.weaver.employee.repository.EmployeeRepository;
import com.sbproject.weaver.file.entity.FileEntity;
import com.sbproject.weaver.file.service.FileService;
import com.sbproject.weaver.file.type.FilePurpose;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService{

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;
    private final FileService fileService;

    @Override
    @Transactional(readOnly = true)
    public CursorPageResponse<EmployeeDto> findAll(EmployeeSearchCondition condition) {
        return employeeRepository.search(condition);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeTrendDto> getTrend(EmployeeTrendCondition condition) {
        return employeeRepository.getTrend(condition);
    }

    @Override
    @Transactional(readOnly = true)
    public Long count(EmployeeCountCondition condition) {
        return employeeRepository.countEmployees(condition);
    }


    @Override
    public List<EmployeeDistributionDto> getDistribution(EmployeeSearchDistribution searchDistribution) {
        return employeeRepository.distribution(searchDistribution);
    }
}
