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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackupService {

    private final BackupRepository backupRepository;
    private final EmployeeRepository employeeRepository;
    private final FileService fileService;
    private final BackupMapper backupMapper;

    @Transactional
    public BackupDto runBackup(String worker) {
        BackupEntity backup = BackupEntity.builder()
                .worker(worker)
                .startedAt(Instant.now())
                .build();

        Instant lastSuccessTime = backupRepository.findFirstByStatusOrderByStartedAtDesc(BackupStatus.COMPLETED)
                .map(BackupEntity::getEndedAt)
                .orElse(Instant.MIN);

        boolean needsBackup = employeeRepository.existsByUpdatedAtAfter(lastSuccessTime);

        if (!needsBackup) {
            backup.skip(Instant.now());
            backupRepository.save(backup);
            return backupMapper.toBackupDto(backup);
        }

        try {
            byte[] csvFile = createCsvFile();
            String fileName = String.format(
                    "employee_backup_%s_%s.csv",
                    backup.getId().toString().substring(0, 8),
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            );

            FileEntity savedFile = fileService.saveBytes(
                    fileName,
                    "text/csv",
                    csvFile,
                    FilePurpose.BACKUP_CSV
            );

            backup.complete(Instant.now(), savedFile);
        } catch (Exception e) {
            log.error("백업 작업 중 오류 발생", e);

            byte[] errorLog = e.toString().getBytes(StandardCharsets.UTF_8);

            String fileName = String.format(
                    "error_%s.log",
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            );

            FileEntity errorFile = fileService.saveBytes(
                    fileName,
                    "text/plain",
                    errorLog,
                    FilePurpose.BACKUP_LOG
            );

            backup.fail(Instant.now(), errorFile);
        }

        backupRepository.save(backup);
        return backupMapper.toBackupDto(backup);
    }

    @Transactional(readOnly = true)
    public BackupDto getLatestBackup(BackupStatus status) {
        BackupEntity backup = backupRepository.findFirstByStatusOrderByStartedAtDesc(status)
                .orElseThrow(() -> new IllegalArgumentException("백업 이력이 없습니다. status=" + status));

        return backupMapper.toBackupDto(backup);
    }

    @Transactional(readOnly = true)
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
    ) {
        Instant fromInstant = parseInstantOrNull(from);
        Instant toInstant = parseInstantOrNull(to);

        String activeCursor = cursor != null && !cursor.isBlank()
                ? cursor
                : idAfter;

        int limit = size + 1;

        List<BackupDto> fetched = backupRepository.findBackups(
                worker,
                status,
                fromInstant,
                toInstant,
                activeCursor,
                sortDirection,
                sortField,
                limit
        );

        boolean hasNext = fetched.size() > size;

        List<BackupDto> content = hasNext
                ? fetched.subList(0, size)
                : fetched;

        String nextCursor = null;

        if (hasNext && !content.isEmpty()) {
            BackupDto last = content.get(content.size() - 1);
            nextCursor = last.getId().toString();
        }

        return CursorPageResponse.<BackupDto>builder()
                .content(content)
                .nextCursor(nextCursor)
                .nextIdAfter(null)
                .size(size)
                .totalElements(content.size())
                .hasNext(hasNext)
                .build();
    }

    private byte[] createCsvFile() {
        List<Employee> employees = employeeRepository.findAll();

        try (
                StringWriter writer = new StringWriter();
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                        .setHeader(
                                "id",
                                "employeeNumber",
                                "name",
                                "email",
                                "departmentId",
                                "position",
                                "hireDate",
                                "status"
                        )
                        .build())
        ) {
            for (Employee employee : employees) {
                csvPrinter.printRecord(
                        employee.getId(),
                        employee.getEmployeeNumber(),
                        employee.getName(),
                        employee.getEmail(),
                        employee.getDepartment().getId(),
                        employee.getPosition(),
                        employee.getHireDate(),
                        employee.getStatus()
                );
            }

            csvPrinter.flush();
            return writer.toString().getBytes(StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new RuntimeException("CSV 파일 생성 실패", e);
        }
    }

    private Instant parseInstantOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return Instant.parse(value);
    }
}