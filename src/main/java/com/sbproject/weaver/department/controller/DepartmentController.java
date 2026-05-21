package com.sbproject.weaver.department.controller;

import com.sbproject.weaver.common.dto.CursorPageResponse;
import com.sbproject.weaver.department.dto.CreateRequest;
import com.sbproject.weaver.department.dto.DepartmentDto;
import com.sbproject.weaver.department.dto.DepartmentSearchRequest;
import com.sbproject.weaver.department.service.DepartmentService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/departments")
public class DepartmentController {

  private final DepartmentService departmentService;

  @GetMapping
  public ResponseEntity<CursorPageResponse<DepartmentDto>> findAll(
          @RequestParam(required = false) UUID cursor,
          @RequestParam(defaultValue = "10") int size,
          @ModelAttribute DepartmentSearchRequest search
  ) {
    CursorPageResponse<DepartmentDto> response = departmentService.findSlice(cursor, size, search);
    return ResponseEntity.ok(response);

  }

  @PostMapping
  public ResponseEntity<DepartmentDto> create(@RequestBody CreateRequest request) {
    DepartmentDto createdDepartment = departmentService.create(request);
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(createdDepartment);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<DepartmentDto> update(@PathVariable UUID id, @RequestBody CreateRequest request) {
    DepartmentDto updateDepartment = departmentService.update(id, request);
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(updateDepartment);
  }



  @DeleteMapping("/{id}")
  public ResponseEntity<DepartmentDto> delete(@PathVariable UUID id) {
    DepartmentDto deleteDepartment = departmentService.delete(id);
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(deleteDepartment);
  }

  @GetMapping("/{id}")
  public ResponseEntity<DepartmentDto> getDepartmentDetail(@PathVariable UUID id) {
    DepartmentDto department = departmentService.findById(id);
    return ResponseEntity.ok(department);
  }

}