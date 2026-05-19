package com.sbproject.weaver.file.storage;

import com.sbproject.weaver.config.FileConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class FileStorage {

    private final FileConfig fileConfig;

    public void save(String storagePath, byte[] bytes) {
        Path targetPath = fileConfig.getUploadDir().resolve(storagePath);

        try {
            Files.createDirectories(targetPath.getParent());
            Files.write(targetPath, bytes);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장을 실패하였습니다. : " + storagePath, e);
        }
    }

    public byte[] read(String storagePath) {
        Path targetPath = fileConfig.getUploadDir().resolve(storagePath);

        try {
            return Files.readAllBytes(targetPath);
        } catch (IOException e) {
            throw new RuntimeException("파일 읽기를 실패하였습니다. : " + storagePath, e);
        }
    }

    public void delete(String storagePath) {
        Path targetPath = fileConfig.getUploadDir().resolve(storagePath);

        try {
            Files.deleteIfExists(targetPath);
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제를 실패하였습니다. : " + storagePath , e);
        }
    }
}
