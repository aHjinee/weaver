package com.sbproject.weaver.changelog.mapper;

import com.sbproject.weaver.changelog.dto.ChangeLogDto;
import com.sbproject.weaver.changelog.dto.DiffDto;
import com.sbproject.weaver.changelog.entity.EmployeeChangeDiff;
import com.sbproject.weaver.changelog.entity.EmployeeChangeLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChangeLogMapper {

    ChangeLogDto roResponse(EmployeeChangeLog entity);

    @Mapping(source = "beforeValue", target = "before")
    @Mapping(source = "afterValue", target = "after")
    DiffDto toDiffDto(EmployeeChangeDiff diff);
}
