package com.sbproject.weaver.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
@Configuration
public class FileConfig {

    private Path uploadDir;

    @PostConstruct
    public void init() {
        uploadDir = Paths.get(System.getProperty("user.dir"), "uploads");

        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("업로드 디렉토리 생성 실패: " + uploadDir.toAbsolutePath(), e);
        }
    }
}
