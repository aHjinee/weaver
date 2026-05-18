package com.sbproject.weaver.department.service;

import com.sbproject.weaver.department.dto.CreateRequest;
import com.sbproject.weaver.department.dto.DepartmentDto;
import java.util.UUID;

public interface DepartmentService {
  DepartmentDto create(CreateRequest request);
  DepartmentDto update(UUID id, CreateRequest request);
}
