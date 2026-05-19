package com.sbproject.weaver.backup.scheduler;

import com.sbproject.weaver.backup.service.BackupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyScheduler {
    private final BackupService backupService;

    @Scheduled(fixedDelayString = "${weaver.backup.interval}")
    public void task() {
        log.info("정기 백업 스케줄러 작업 시작");
        try {
            backupService.runBackup("system");
            log.info("정기 백업 스케출러 작업 완료");
        } catch (Exception e) {
            log.error("정기 백업 스케줄러 작업 중 예외 발생", e);
        }
    }
}