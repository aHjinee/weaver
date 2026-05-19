package com.sbproject.weaver.backup.controller;

import com.sbproject.weaver.backup.dto.BackupDto;
import com.sbproject.weaver.backup.entity.BackupStatus;
import com.sbproject.weaver.backup.service.BackupService;
import com.sbproject.weaver.common.dto.CursorPageResponse;
import com.sbproject.weaver.common.util.ipUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/backups")
public class BackupController {

    private final BackupService backupService;

    @GetMapping
    public ResponseEntity<CursorPageResponse<BackupDto>> findAll(
            @RequestParam(required = false) String worker,
            @RequestParam(required = false) BackupStatus status,
            @RequestParam(required = false) String startedAtFrom,
            @RequestParam(required = false) String startedAtTo,
            @RequestParam(required = false) String idAfter,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startedAt") String sortField,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        return ResponseEntity.ok(backupService.findBackups(
                worker,
                status,
                startedAtFrom,
                startedAtTo,
                cursor,
                idAfter,
                size,
                sortField,
                sortDirection
        ));
    }

    @PostMapping
    public ResponseEntity<BackupDto> create(HttpServletRequest request) {
        String ip = ipUtil.getClientIp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(backupService.runBackup(ip));
    }

    @GetMapping("/latest")
    public ResponseEntity<BackupDto> getLatestBackup(
            @RequestParam(value = "status", required = false, defaultValue = "COMPLETED") BackupStatus status
    ) {
        BackupDto latestBackup = backupService.getLatestBackup(status);
        return ResponseEntity.status(HttpStatus.OK).body(latestBackup);
    }
}