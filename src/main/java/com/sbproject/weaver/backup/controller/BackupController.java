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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/backups")
public class BackupController {

    private final BackupService backupService;

    @PostMapping
    public ResponseEntity<BackupDto> create(HttpServletRequest request) {
        String ip = ipUtil.getClientIp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(backupService.runBackup(ip));
    }

    @GetMapping("/latest")
    public ResponseEntity<BackupDto> getLatestBackup(
            @RequestParam(value = "status", required = false, defaultValue = "COMPLETED") BackupStatus status) {
        BackupDto latestBackup = backupService.getLatestBackup(status);
        return ResponseEntity.status(HttpStatus.OK).body(latestBackup);
    }
}
