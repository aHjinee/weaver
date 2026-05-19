package com.sbproject.weaver.file.mapper;

import com.sbproject.weaver.file.dto.FileResponse;
import com.sbproject.weaver.file.entity.FileEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileMapper {
    FileResponse toResponse(FileEntity fileEntity);
}
