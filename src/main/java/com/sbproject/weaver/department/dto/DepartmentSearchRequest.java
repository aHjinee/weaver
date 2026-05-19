package com.sbproject.weaver.department.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentSearchRequest {
    private String nameOrDescription;
    private UUID idAfter;
    private String sortField;
    private String sortDirection;
}
