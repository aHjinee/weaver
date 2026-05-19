package com.sbproject.weaver.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileCreateRequest {
    private String directory;
    private String originalName;
    private String contentType;
    private byte[] bytes;
}