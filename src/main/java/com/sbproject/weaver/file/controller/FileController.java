package com.sbproject.weaver.file.controller;

import com.sbproject.weaver.file.dto.FileResponse;
import com.sbproject.weaver.file.entity.FileEntity;
import com.sbproject.weaver.file.mapper.FileMapper;
import com.sbproject.weaver.file.service.FileService;
import com.sbproject.weaver.file.type.FilePurpose;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test/files")
public class FileController {

    private final FileService fileService;
    private final FileMapper fileMapper;

    // 프로필 이미지 업로드 테스트
    @PostMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileResponse> uploadProfile(
            @RequestPart("file") MultipartFile file
    ) {
        FileEntity saved = fileService.save(file, FilePurpose.PROFILE);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(fileMapper.toResponse(saved));
    }

    // 백업 CSV 저장 테스트
    @PostMapping("/backup-csv")
    public ResponseEntity<FileResponse> createBackupCsv() {
        String csv = """
                employeeNumber,name,email,department,status
                EMP-2024-0001,김영호,youngho.kim@hrbank.com,개발팀,재직중
                EMP-2024-0002,이민지,minji.lee@hrbank.com,인사팀,재직중
                """;

        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);

        FileEntity saved = fileService.save(
                "employee-backup-test.csv",
                "text/csv",
                bytes,
                FilePurpose.BACKUP_CSV
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(fileMapper.toResponse(saved));
    }

    // 백업 실패 로그 저장 테스트
    @PostMapping("/backup-log")
    public ResponseEntity<FileResponse> createBackupLog() {
        String log = """
                backup failed.
                reason: test exception
                message: 테스트용 백업 실패 로그입니다.
                """;

        byte[] bytes = log.getBytes(StandardCharsets.UTF_8);

        FileEntity saved = fileService.save(
                "employee-backup-failed-test.log",
                "text/plain",
                bytes,
                FilePurpose.BACKUP_LOG
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(fileMapper.toResponse(saved));
    }

    // 파일 메타데이터 조회 테스트
    @GetMapping("/{fileId}")
    public ResponseEntity<FileResponse> findById(@PathVariable UUID fileId) {
        FileResponse response = fileService.findById(fileId);
        return ResponseEntity.ok(response);
    }

    // 파일 다운로드 테스트
    @GetMapping("/{fileId}/download")
    public ResponseEntity<byte[]> download(@PathVariable UUID fileId) {
        FileResponse file = fileService.findById(fileId);
        byte[] bytes = fileService.download(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getOriginalName() + "\""
                )
                .body(bytes);
    }

    // 파일 삭제 테스트
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> delete(@PathVariable UUID fileId) {
        fileService.delete(fileId);
        return ResponseEntity.noContent().build();
    }
}