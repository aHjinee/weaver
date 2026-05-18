package com.sbproject.weaver.department.controller;

import com.sbproject.weaver.department.dto.CreateRequest;
import com.sbproject.weaver.department.dto.DepartmentDto;
import com.sbproject.weaver.department.service.DepartmentService;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

  private final DepartmentService departmentService;
  public DepartmentController(DepartmentService departmentService) {
    this.departmentService = departmentService;
  }

  @PostMapping
  public ResponseEntity<DepartmentDto> create(@RequestBody CreateRequest request) {
    DepartmentDto createdDepartment = departmentService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdDepartment);
  }

  @PutMapping("/{id}")
  public ResponseEntity<DepartmentDto> update(@PathVariable UUID id, @RequestBody CreateRequest request) {
    DepartmentDto updateDepartment = departmentService.update(id, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updateDepartment);
  }


}
