package com.sbproject.weaver.employee.controller;

import com.sbproject.weaver.common.dto.CursorPageResponse;
import com.sbproject.weaver.employee.dto.*;
import com.sbproject.weaver.employee.entity.EmployeeStatus;
import com.sbproject.weaver.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<CursorPageResponse<EmployeeDto>> findAll(
            @RequestParam(required = false) String nameOrEmail,
            @RequestParam(required = false) String employeeNumber,
            @RequestParam(required = false) String departmentName,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) LocalDate hireDateFrom,
            @RequestParam(required = false) LocalDate hireDateTo,
            @RequestParam(required = false) EmployeeStatus status,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Long idAfter,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "name") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        EmployeeSearchCondition condition = new EmployeeSearchCondition(
                nameOrEmail,
                employeeNumber,
                departmentName,
                position,
                hireDateFrom,
                hireDateTo,
                status,
                cursor,
                idAfter,
                size,
                sortField,
                sortDirection
        );

        CursorPageResponse<EmployeeDto> response = employeeService.findAll(condition);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats/trend")
    public ResponseEntity<List<EmployeeTrendDto>> getTrend(
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(defaultValue = "month") String unit
    ) {
        LocalDate resolvedTo = to != null ? to : LocalDate.now();
        LocalDate resolvedFrom = from != null ? from : resolvedTo.minusYears(1);

        EmployeeTrendCondition condition = new EmployeeTrendCondition(
                resolvedFrom,
                resolvedTo,
                unit
        );

        List<EmployeeTrendDto> response = employeeService.getTrend(condition);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countEmployees(
            @RequestParam(required = false) EmployeeStatus status,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to
    ) {
        EmployeeCountCondition condition = new EmployeeCountCondition(
                status,
                from,
                to
        );

        Long response = employeeService.count(condition);
        return ResponseEntity.ok(response);
    }

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