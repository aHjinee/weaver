package com.sbproject.weaver.backup.mapper;

import com.sbproject.weaver.backup.dto.BackupDto;
import com.sbproject.weaver.backup.entity.BackupEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BackupMapper {
    BackupDto toBackupDto(BackupEntity backupEntity);
}
