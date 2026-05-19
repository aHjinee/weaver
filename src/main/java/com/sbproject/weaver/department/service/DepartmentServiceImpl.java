package com.sbproject.weaver.department.service;

import com.sbproject.weaver.department.dto.CursorPageResponseDepartmentDto;
import com.sbproject.weaver.department.dto.DepartmentDto;
import com.sbproject.weaver.department.dto.DepartmentSearchRequest;
import com.sbproject.weaver.department.entity.Department;
import com.sbproject.weaver.department.repository.DepartmentRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepositoryImpl departmentRepository;

    @Transactional
    public CursorPageResponseDepartmentDto findSlice(UUID cursor, int size, DepartmentSearchRequest search){
        Slice<DepartmentDto> slice = departmentRepository.searchSlice(cursor, size, search);
        long totalElements = departmentRepository.countSearch(search);
        return CursorPageResponseDepartmentDto.from(slice, size, totalElements);
    }
}
