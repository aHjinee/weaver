package com.sbproject.weaver.department.dto;

import com.sbproject.weaver.department.entity.Department;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateRequest{
    private final String name;
    private final String description;
    private final LocalDate establishedDate;

  public CreateRequest(String name, String description, LocalDate foundedDate) {
    this.name = name;
    this.description = description;
    this.establishedDate = foundedDate;
  }

  public Department toEntity() {
    return Department.builder()
        .name(name)
        .description(description)
        .establishedDate(establishedDate)
        .build();
  }

}
