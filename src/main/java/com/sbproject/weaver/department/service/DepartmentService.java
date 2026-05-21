package com.sbproject.weaver.department.service;

import com.sbproject.weaver.common.dto.CursorPageResponse;
import com.sbproject.weaver.department.dto.CreateRequest;
import com.sbproject.weaver.department.dto.DepartmentDto;
import com.sbproject.weaver.department.dto.DepartmentSearchRequest;
import java.util.UUID;

public interface DepartmentService {

  CursorPageResponse<DepartmentDto> findSlice(UUID cursor, int size, DepartmentSearchRequest search);
  DepartmentDto create(CreateRequest request);
  DepartmentDto update(UUID id, CreateRequest request);
  DepartmentDto delete(UUID id);
  DepartmentDto findById(UUID id);
}