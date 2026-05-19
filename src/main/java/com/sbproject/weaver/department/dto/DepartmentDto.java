package com.sbproject.weaver.department.dto;

import com.sbproject.weaver.department.entity.Department;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentDto {
  private UUID id;
  private String name;
  private String description;
  private LocalDate establishedDate;
  private int employeeCount;

  public static DepartmentDto from(Department department) {
    return DepartmentDto.builder()
        .id(department.getId())
        .name(department.getName())
        .description(department.getDescription())
        .establishedDate(department.getEstablishedDate())
        .employeeCount(0) // 임시 나중에 채워야함 !!!!!!!!!!!!!
        .build();
  }
}
