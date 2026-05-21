package com.sbproject.weaver.employee.controller;

import com.sbproject.weaver.common.dto.CursorPageResponse;
import com.sbproject.weaver.employee.dto.EmployeeCountCondition;
import com.sbproject.weaver.employee.dto.EmployeeCreateRequest;
import com.sbproject.weaver.employee.dto.EmployeeDistributionDto;
import com.sbproject.weaver.employee.dto.EmployeeDto;
import com.sbproject.weaver.employee.dto.EmployeeSearchCondition;
import com.sbproject.weaver.employee.dto.EmployeeSearchDistribution;
import com.sbproject.weaver.employee.dto.EmployeeTrendCondition;
import com.sbproject.weaver.employee.dto.EmployeeTrendDto;
import com.sbproject.weaver.employee.dto.EmployeeUpdateRequest;
import com.sbproject.weaver.employee.entity.EmployeeStatus;
import com.sbproject.weaver.employee.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<EmployeeDto> create(
            HttpServletRequest httpRequest,
            @RequestPart("employee") EmployeeCreateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        EmployeeDto response = employeeService.create(request, profile, httpRequest);
        return ResponseEntity.ok(response);
    }

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

    @GetMapping("/count")
    public ResponseEntity<Long> count(
            @RequestParam(required = false) EmployeeStatus status,
            @RequestParam(name = "fromDate", required = false) LocalDate fromDate,
            @RequestParam(name = "toDate", required = false) LocalDate toDate,
            @RequestParam(name = "hireDateFrom", required = false) LocalDate hireDateFrom,
            @RequestParam(name = "hireDateTo", required = false) LocalDate hireDateTo
    ) {
        LocalDate resolvedFrom = fromDate != null ? fromDate : hireDateFrom;
        LocalDate resolvedTo = toDate != null ? toDate : hireDateTo;

        EmployeeCountCondition condition = new EmployeeCountCondition(
                status,
                resolvedFrom,
                resolvedTo
        );

        Long response = employeeService.count(condition);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats/trend")
    public ResponseEntity<List<EmployeeTrendDto>> getTrend(
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(defaultValue = "month") String unit
    ) {
        LocalDate resolvedTo = to != null ? to : LocalDate.now();

        LocalDate resolvedFrom = from != null
                ? from
                : switch (unit) {
            case "day" -> resolvedTo.minusDays(25);
            case "week" -> resolvedTo.minusDays(49);
            case "month" -> resolvedTo.minusMonths(10);
            case "quarter" -> resolvedTo.minusYears(2);
            case "year" -> resolvedTo.minusYears(12);
            default -> resolvedTo.minusYears(1);
        };

        EmployeeTrendCondition condition = new EmployeeTrendCondition(
                resolvedFrom,
                resolvedTo,
                unit
        );

        List<EmployeeTrendDto> response = employeeService.getTrend(condition);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats/distribution")
    public ResponseEntity<List<EmployeeDistributionDto>> getDistribution(
            @RequestParam(required = false) String groupBy,
            @RequestParam(required = false) EmployeeStatus status
    ) {
        EmployeeSearchDistribution condition = EmployeeSearchDistribution.builder()
                .groupBy(groupBy)
                .status(status)
                .build();

        List<EmployeeDistributionDto> response = employeeService.getDistribution(condition);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> findById(
            @PathVariable UUID id
    ) {
        EmployeeDto response = employeeService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EmployeeDto> update(
            HttpServletRequest httpRequest,
            @PathVariable UUID id,
            @RequestPart("employee") EmployeeUpdateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        EmployeeDto response = employeeService.update(id, request, profile, httpRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            HttpServletRequest httpRequest,
            @PathVariable UUID id
    ) {
        employeeService.delete(id, httpRequest);
        return ResponseEntity.noContent().build();
    }
}
