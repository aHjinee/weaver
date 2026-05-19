package com.sbproject.weaver.file.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FilePurpose {
    PROFILE("profiles", null),
    BACKUP_CSV("backups", "text/csv"),
    BACKUP_LOG("logs", "text/plain");

    private final String directory;
    private final String fixedContentType;
}