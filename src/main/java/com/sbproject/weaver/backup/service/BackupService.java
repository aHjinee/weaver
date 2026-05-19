package com.sbproject.weaver.backup.service;

import com.sbproject.weaver.backup.dto.BackupDto;
import com.sbproject.weaver.backup.entity.BackupEntity;
import com.sbproject.weaver.backup.entity.BackupStatus;
import com.sbproject.weaver.backup.mapper.BackupMapper;
import com.sbproject.weaver.backup.repository.BackupRepository;
import com.sbproject.weaver.common.dto.CursorPageResponse;
import com.sbproject.weaver.employee.entity.Employee;
import com.sbproject.weaver.employee.repository.EmployeeRepository;
import com.sbproject.weaver.file.entity.FileEntity;
import com.sbproject.weaver.file.service.FileService;
import com.sbproject.weaver.file.type.FilePurpose;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BackupService {
    private final EmployeeRepository employeeRepository;
    private final BackupRepository backupRepository;
    private final BackupMapper backupMapper;
    private final FileService fileService;

    @Transactional
    public BackupDto runBackup(String worker) {
        BackupEntity backup = BackupEntity.builder().worker(worker).startedAt(Instant.now()).build();

        Instant lastSuccessTime = backupRepository.findFirstByStatusOrderByStartedAtDesc(BackupStatus.COMPLETED)
                .map(BackupEntity::getEndedAt)
                .orElse(Instant.MIN);

        boolean needsBackup = employeeRepository.existsByUpdatedAtAfter(lastSuccessTime);
        if (!needsBackup) {
            backup.skip(Instant.now());
            backupRepository.save(backup);
            return backupMapper.toBackupDto(backup);
        }

        backupRepository.save(backup);

        try {
            byte[] csvFile = createCsvFile();
            String fileName = String.format("employee_backup_%s_%s.csv",
                    backup.getId().toString().substring(0, 8),
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

            FileEntity savedFile = fileService.save(fileName, "text/csv", csvFile, FilePurpose.BACKUP_CSV);
            backup.complete(Instant.now(), savedFile);
        } catch (Exception e) {
            log.error("백업 작업 중 오류 발생", e);
            byte[] errorLog = e.toString().getBytes(StandardCharsets.UTF_8);
            String fileName = String.format("error_%s.log",
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

            FileEntity errorFile = fileService.save(fileName, "text/plain", errorLog, FilePurpose.BACKUP_LOG);
            backup.fail(Instant.now(), errorFile);
        }

        return backupMapper.toBackupDto(backup);
    }

    private byte[] createCsvFile() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        stream.write(0xEF);
        stream.write(0xBB);
        stream.write(0xBF);

        try (OutputStreamWriter writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                     .setHeader("ID", "직원번호", "이름", "이메일", "부서", "직급", "입사일", "상태")
                     .build())) {
            List<Employee> employees = employeeRepository.findAll();
            for (Employee employee : employees) {
                printer.printRecord(
                        employee.getId(),
                        employee.getEmployeeNumber(),
                        employee.getName(),
                        employee.getEmail(),
                        employee.getDepartmentId(),
                        employee.getPosition(),
                        employee.getHireDate(),
                        employee.getStatus()
                );
            }
            printer.flush();
        }

        return stream.toByteArray();
    }

    @Transactional(readOnly = true)
    public BackupDto getLatestBackup(BackupStatus status) {
        return backupRepository.findFirstByStatusOrderByStartedAtDesc(status)
                .map(backupMapper::toBackupDto)
                .orElse(null);
    }
}
