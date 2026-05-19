package com.sbproject.weaver.department.controller;

import com.sbproject.weaver.department.dto.CursorPageResponseDepartmentDto;
import com.sbproject.weaver.department.dto.DepartmentSearchRequest;
import com.sbproject.weaver.department.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/departments")
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<CursorPageResponseDepartmentDto> findAll(
            @RequestParam(required = false) UUID cursor,
            @RequestParam(defaultValue = "10") int size,
            @ModelAttribute DepartmentSearchRequest search
            ) {
        CursorPageResponseDepartmentDto response = departmentService.findSlice(cursor, size, search);
        return ResponseEntity.ok(response);

    }
}
