package com.sbproject.weaver.file.dto;


import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileResponse {
    private UUID id;
    private String originalName;
    private String contentType;
    private Long size;
    private String storagePath;
    private Instant createdAt;
}
