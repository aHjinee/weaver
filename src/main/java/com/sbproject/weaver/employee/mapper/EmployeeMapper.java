package com.sbproject.weaver.employee.mapper;

import com.sbproject.weaver.employee.dto.EmployeeDto;
import com.sbproject.weaver.employee.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(source = "department.name", target = "departmentName")
    @Mapping(source = "profileImage.id", target = "profileImageId")
    EmployeeDto toDto(Employee employee);
}