package com.sbproject.weaver.department.repository;

import com.sbproject.weaver.common.dto.CursorPageResponse;
import com.sbproject.weaver.department.dto.DepartmentDto;
import com.sbproject.weaver.department.dto.DepartmentSearchRequest;
import com.sbproject.weaver.department.entity.Department;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Slice;

public interface DepartmentRepositoryCustom {
  Optional<DepartmentDto> findById(UUID id);
  CursorPageResponse<DepartmentDto> searchSlice(UUID cursor, int size, DepartmentSearchRequest search);
  long countSearch(DepartmentSearchRequest search);

}
