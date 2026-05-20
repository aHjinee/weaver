package com.sbproject.weaver.department.service;

import com.sbproject.weaver.common.dto.CursorPageResponse;
import com.sbproject.weaver.department.dto.CreateRequest;
import com.sbproject.weaver.department.dto.DepartmentDto;
import com.sbproject.weaver.department.dto.DepartmentSearchRequest;
import com.sbproject.weaver.department.entity.Department;
import com.sbproject.weaver.department.repository.DepartmentRepository;
import com.sbproject.weaver.department.repository.DepartmentRepositoryCustom;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

  private final DepartmentRepositoryCustom departmentRepositoryCustom;
  private final DepartmentRepository departmentRepository;

  @Transactional
  public CursorPageResponse<DepartmentDto> findSlice(UUID cursor, int size, DepartmentSearchRequest search){
    return departmentRepositoryCustom.searchSlice(cursor, size, search);
  }

  @Override
  public DepartmentDto create(CreateRequest request) {
    Department department = request.toEntity();

    // name 중복 체크
    if(departmentRepository.existsByName(department.getName())) {
      throw new IllegalArgumentException("Department name already exists");
    }

    // 비어있는지 확인
    if(department.getName() == null || department.getName().isEmpty()){
      throw new IllegalArgumentException("Department name cannot be empty");
    }
    if(department.getDescription() == null || department.getDescription().isEmpty()){
      throw new IllegalArgumentException("Department description cannot be empty");
    }
    if (department.getEstablishedDate() == null) {
      throw new IllegalArgumentException("Department foundedDate cannot be empty");
    }

    Department savedDepartment = departmentRepository.save(department);

    return DepartmentDto.from(savedDepartment);
  }

  @Override
  public DepartmentDto update(UUID id, CreateRequest request) {


    Department entity = departmentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Department not found"));

    // 비어있는지 확인
    if(request.getName() == null || request.getName().isEmpty()){
      throw new IllegalArgumentException("Department name cannot be empty");
    }
    if(request.getDescription() == null || request.getDescription().isEmpty()){
      throw new IllegalArgumentException("Department description cannot be empty");
    }
    if (request.getEstablishedDate() == null) {
      throw new IllegalArgumentException("Department foundedDate cannot be empty");
    }

    // name 중복 체크
    if(departmentRepository.existsByName(request.getName())
    && !entity.getName().equals(request.getName())) {
      throw new IllegalArgumentException("Department name already exists");
    }

    entity.update(request.getName(), request.getDescription(),request.getEstablishedDate());

    Department savedDepartment = departmentRepository.save(entity);

    return DepartmentDto.from(savedDepartment);
  }

  @Override
  public DepartmentDto delete(UUID id) {
    DepartmentDto dto = departmentRepositoryCustom.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 부서를 찾을 수 없습니다."));
    if(dto.getEmployeeCount() > 0) {
      throw new IllegalStateException("소속 직원이 있는 부서는 삭제할 수 없습니다");
    }
    else departmentRepository.deleteById(id);

    return  dto;
  }

  @Override
  public DepartmentDto findById(UUID id) {
    return departmentRepositoryCustom.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("부서를 찾을 수 없습니다. ID: " + id));
  }
}
