package com.sbproject.weaver.department.repository;

import com.sbproject.weaver.department.dto.DepartmentDto;
import com.sbproject.weaver.department.dto.DepartmentSearchRequest;
import org.springframework.data.domain.Slice;

import java.util.UUID;

public interface DepartmentRepository {

    Slice<DepartmentDto> searchSlice(UUID cursor, int size, DepartmentSearchRequest search);
    long countSearch(DepartmentSearchRequest search);
}
