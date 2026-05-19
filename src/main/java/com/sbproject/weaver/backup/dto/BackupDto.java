package com.sbproject.weaver.backup.dto;

import com.sbproject.weaver.backup.entity.BackupStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BackupDto {
    private UUID id;
    private String worker;
    private Instant startedAt;
    private Instant endedAt;
    private BackupStatus status;
    private UUID fileId;
}
