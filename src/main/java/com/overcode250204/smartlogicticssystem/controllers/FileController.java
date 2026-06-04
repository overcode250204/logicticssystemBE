package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.base.BaseController;
import com.overcode250204.smartlogicticssystem.base.BaseResponse;
import com.overcode250204.smartlogicticssystem.dtos.response.FileUploadResponseDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.PresignedUrlResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.User;
import com.overcode250204.smartlogicticssystem.enums.FinanceAuditAction;
import com.overcode250204.smartlogicticssystem.repositories.UserRepository;
import com.overcode250204.smartlogicticssystem.services.IFileStorageService;
import com.overcode250204.smartlogicticssystem.services.IFinanceTransactionAuditLogService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final IFinanceTransactionAuditLogService auditLogService;
    private final UserRepository userRepository;

    @PostMapping(value = "/receipts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<FileUploadResponseDTO>> uploadReceipt(
            @RequestParam("file") MultipartFile file,
            @RequestHeader(name = "X-User-Id", required = false) Integer userId,
            HttpServletRequest httpServletRequest
    ) {
        FileUploadResponseDTO response = fileStorageService.uploadReceipt(file);
        auditLogService.log(null, FinanceAuditAction.UPLOAD_RECEIPT, null, null,
                null, response, findUser(userId), getIpAddress(httpServletRequest), getUserAgent(httpServletRequest),
                "Receipt uploaded: " + response.getReceiptImageKey());
        return success(response, "Receipt image uploaded successfully");
    }

    @GetMapping("/presigned-url")
    public ResponseEntity<BaseResponse<PresignedUrlResponseDTO>> createPresignedUrl(@RequestParam String key) {
        return success(fileStorageService.createPresignedUrl(key), "Presigned URL created successfully");
    }

    private User findUser(Integer userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById((long) userId).orElse(null);
    }

    private String getIpAddress(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
}
