package com.sbproject.weaver.employee.controller;

import com.sbproject.weaver.employee.dto.EmployeeDistributionDto;
import com.sbproject.weaver.employee.dto.EmployeeSearchDistribution;
import com.sbproject.weaver.employee.entity.EmployeeStatus;
import com.sbproject.weaver.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/stats/distribution")
    public ResponseEntity<List<EmployeeDistributionDto>> getDistribution(
            @RequestParam(required = false) String groupBy,
            @RequestParam(required = false) EmployeeStatus status
    ) {

        EmployeeSearchDistribution search = EmployeeSearchDistribution.builder()
                .groupBy(groupBy)
                .status(status)
                .build();

        List<EmployeeDistributionDto> response = employeeService.getDistribution(search);
        return ResponseEntity.ok(response);
    }
}
