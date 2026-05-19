package com.sbproject.weaver.backup.repository;

import com.sbproject.weaver.backup.dto.BackupDto;
import com.sbproject.weaver.backup.entity.BackupStatus;

import java.time.Instant;
import java.util.List;

public interface BackupRepositoryCustom {

    List<BackupDto> findBackups(
            String worker,
            BackupStatus status,
            Instant from,
            Instant to,
            String cursor,
            String direction,
            String sortField,
            int limit
    );
}