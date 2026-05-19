package com.sbproject.weaver.department.service;

import com.sbproject.weaver.department.dto.CreateRequest;
import com.sbproject.weaver.department.dto.CursorPageResponseDepartmentDto;
import com.sbproject.weaver.department.dto.DepartmentDto;
import com.sbproject.weaver.department.dto.DepartmentSearchRequest;
import java.util.UUID;

public interface DepartmentService {

  CursorPageResponseDepartmentDto findSlice(UUID cursor, int size, DepartmentSearchRequest search);
  DepartmentDto create(CreateRequest request);
  DepartmentDto update(UUID id, CreateRequest request);
  DepartmentDto delete(UUID id);
}
