package com.sbproject.weaver.backup.service;

import com.sbproject.weaver.backup.dto.BackupDto;
import com.sbproject.weaver.backup.entity.BackupStatus;
import com.sbproject.weaver.common.dto.CursorPageResponse;

public interface BackupService {
    public BackupDto runBackup(String worker);

    public BackupDto getLatestBackup(BackupStatus status);

    public CursorPageResponse<BackupDto> findBackups(
            String worker,
            BackupStatus status,
            String from,
            String to,
            String cursor,
            String idAfter,
            int size,
            String sortField,
            String sortDirection
    );
}