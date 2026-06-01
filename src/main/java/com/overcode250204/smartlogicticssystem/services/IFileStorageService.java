package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.response.FileUploadResponseDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.PresignedUrlResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface IFileStorageService {
    FileUploadResponseDTO uploadReceipt(MultipartFile file);

    PresignedUrlResponseDTO createPresignedUrl(String key);
}
