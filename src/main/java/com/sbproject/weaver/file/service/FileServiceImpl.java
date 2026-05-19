package com.sbproject.weaver.file.service;

import com.github.f4b6a3.uuid.UuidCreator;
import com.sbproject.weaver.file.dto.FileResponse;
import com.sbproject.weaver.file.entity.FileEntity;
import com.sbproject.weaver.file.mapper.FileMapper;
import com.sbproject.weaver.file.repository.FileRepository;
import com.sbproject.weaver.file.storage.FileStorage;
import com.sbproject.weaver.file.type.FilePurpose;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final FileStorage fileStorage;
    private final FileMapper fileMapper;

    @Override
    public FileEntity saveMultipartFile(MultipartFile multipartFile, FilePurpose purpose) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new IllegalArgumentException("빈 파일은 저장할 수 없습니다.");
        }

        try {
            return saveBytes(
                    multipartFile.getOriginalFilename(),
                    multipartFile.getContentType(),
                    multipartFile.getBytes(),
                    purpose
            );
        } catch (IOException e) {
            throw new RuntimeException("파일 읽기를 실패하였습니다.", e);
        }
    }

    @Override
    public FileEntity saveBytes(String originalName, String contentType, byte[] bytes, FilePurpose purpose) {
        if (purpose == null) {
            throw new IllegalArgumentException("파일 용도는 필수입니다.");
        }

        if (originalName == null || originalName.isBlank()) {
            throw new IllegalArgumentException("파일명이 비어 있습니다.");
        }

        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("빈 파일은 저장할 수 없습니다.");
        }

        UUID id = UuidCreator.getTimeOrderedEpoch();

        String storedFileName = id + "_" + originalName;
        String storagePath = purpose.getDirectory() + "/" + storedFileName;

        fileStorage.save(storagePath, bytes);

        String resolvedContentType = resolveContentType(contentType, purpose);

        FileEntity fileEntity = FileEntity.builder()
                .id(id)
                .originalName(originalName)
                .contentType(resolvedContentType)
                .size((long) bytes.length)
                .storagePath(storagePath)
                .createdAt(Instant.now())
                .build();

        return fileRepository.save(fileEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public FileResponse findById(UUID fileId) {
        FileEntity fileEntity = getFileOrThrow(fileId);
        return fileMapper.toResponse(fileEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] download(UUID fileId) {
        FileEntity fileEntity = getFileOrThrow(fileId);
        return fileStorage.read(fileEntity.getStoragePath());
    }

    @Override
    public void delete(UUID fileId) {
        FileEntity fileEntity = getFileOrThrow(fileId);

        fileStorage.delete(fileEntity.getStoragePath());
        fileRepository.delete(fileEntity);
    }

    private FileEntity getFileOrThrow(UUID fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new NoSuchElementException("파일을 찾을 수 없습니다. id = " + fileId));
    }

    private String resolveContentType(String contentType, FilePurpose purpose) {
        if (purpose.getFixedContentType() != null) {
            return purpose.getFixedContentType();
        }

        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("파일 Content-Type이 비어 있습니다.");
        }

        return contentType;
    }
}