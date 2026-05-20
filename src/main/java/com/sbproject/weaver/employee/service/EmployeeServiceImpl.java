package com.sbproject.weaver.employee.service;

import com.sbproject.weaver.employee.dto.EmployeeDistributionDto;
import com.sbproject.weaver.employee.dto.EmployeeSearchDistribution;
import com.sbproject.weaver.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService{

    private final EmployeeRepository employeeRepository;

    @Override
    public List<EmployeeDistributionDto> getDistribution(EmployeeSearchDistribution searchDistribution) {
        return employeeRepository.distribution(searchDistribution);
    }
}
