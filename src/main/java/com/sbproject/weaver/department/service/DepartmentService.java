package com.sbproject.weaver.department.service;

import com.sbproject.weaver.department.dto.CursorPageResponseDepartmentDto;
import com.sbproject.weaver.department.dto.DepartmentSearchRequest;

import java.util.UUID;

public interface DepartmentService {
    CursorPageResponseDepartmentDto findSlice(UUID cursor, int size, DepartmentSearchRequest search);
}
