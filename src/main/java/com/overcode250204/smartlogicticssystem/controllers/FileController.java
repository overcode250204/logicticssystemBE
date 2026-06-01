package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.response.FileUploadResponseDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.PresignedUrlResponseDTO;
import com.overcode250204.smartlogicticssystem.services.IFileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController extends BaseController {
    private final IFileStorageService fileStorageService;

    @PostMapping(value = "/receipts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<FileUploadResponseDTO>> uploadReceipt(@RequestParam("file") MultipartFile file) {
        return success(fileStorageService.uploadReceipt(file), "Receipt image uploaded successfully");
    }

    @GetMapping("/presigned-url")
    public ResponseEntity<BaseResponse<PresignedUrlResponseDTO>> createPresignedUrl(@RequestParam String key) {
        return success(fileStorageService.createPresignedUrl(key), "Presigned URL created successfully");
    }
}
