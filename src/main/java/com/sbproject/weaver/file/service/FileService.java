package com.sbproject.weaver.file.service;

import com.sbproject.weaver.file.dto.FileResponse;
import com.sbproject.weaver.file.entity.FileEntity;
import com.sbproject.weaver.file.type.FilePurpose;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface FileService {

    FileEntity saveMultipartFile(MultipartFile multipartFile, FilePurpose purpose);

    FileEntity saveBytes(String originalName, String contentType, byte[] bytes, FilePurpose purpose);

    FileResponse findById(UUID fileId);

    byte[] download(UUID fileId);

    void delete(UUID fileId);
}