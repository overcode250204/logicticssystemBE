package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.configs.AwsS3Properties;
import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.StorageErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3FileService {

    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    private final S3Client s3Client;
    private final AwsS3Properties properties;

    public String uploadBytes(byte[] bytes, String key, String contentType) {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("File bytes must not be empty");
        }
        if (!StringUtils.hasText(key)) {
            throw new IllegalArgumentException("File key must not be empty");
        }

        String safeContentType = StringUtils.hasText(contentType) ? contentType : DEFAULT_CONTENT_TYPE;

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(properties.bucketName())
                    .key(key)
                    .contentType(safeContentType)
                    .contentLength((long) bytes.length)
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(bytes));
            return buildPublicUrl(key);
        } catch (S3Exception e) {
            String awsMessage = e.awsErrorDetails() != null ? e.awsErrorDetails().errorMessage() : e.getMessage();
            log.error("Upload file to S3 failed. key={}, bucket={}, awsMessage={}",
                    key, properties.bucketName(), awsMessage, e);
            throw new AppException(StorageErrorCode.FILE_UPLOAD_FAILED);
        } catch (Exception e) {
            log.error("Upload file failed. key={}, bucket={}", key, properties.bucketName(), e);
            throw new AppException(StorageErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    public String uploadFile(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new AppException(StorageErrorCode.INVALID_FILE);
        }

        try {
            String key = buildFileKey(folder, file.getOriginalFilename());
            return uploadBytes(file.getBytes(), key, file.getContentType());
        } catch (IOException e) {
            throw new AppException(StorageErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    public void deleteFile(String key) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(properties.bucketName())
                    .key(key)
                    .build();
            s3Client.deleteObject(request);
        } catch (S3Exception e) {
            throw new AppException(StorageErrorCode.FILE_DELETE_FAILED);
        }
    }

    public String extractKeyFromPublicUrl(String publicUrl) {
        if (!StringUtils.hasText(publicUrl)) {
            throw new AppException(StorageErrorCode.INVALID_FILE);
        }

        try {
            URI uri = URI.create(publicUrl);
            String expectedHost = "%s.s3.%s.amazonaws.com".formatted(
                    properties.bucketName(),
                    properties.region()
            );

            if (!"https".equalsIgnoreCase(uri.getScheme()) || !expectedHost.equalsIgnoreCase(uri.getHost())) {
                throw new AppException(StorageErrorCode.INVALID_FILE);
            }

            String rawPath = uri.getRawPath();
            if (!StringUtils.hasText(rawPath) || "/".equals(rawPath)) {
                throw new AppException(StorageErrorCode.INVALID_FILE);
            }

            return UriUtils.decode(rawPath.replaceFirst("^/+", ""), StandardCharsets.UTF_8);
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(StorageErrorCode.INVALID_FILE);
        }
    }

    public void deleteFileByPublicUrl(String publicUrl) {
        deleteFile(extractKeyFromPublicUrl(publicUrl));
    }

    private String buildPublicUrl(String key) {
        return "https://%s.s3.%s.amazonaws.com/%s".formatted(
                properties.bucketName(),
                properties.region(),
                key
        );
    }

    private String buildFileKey(String folder, String originalFilename) {
        String normalizedFolder = normalizeFolder(folder);
        String filename = StringUtils.hasText(originalFilename) ? originalFilename : "file";
        String safeFilename = filename.replaceAll("[^a-zA-Z0-9._-]", "_");
        return "%s/%s-%s".formatted(normalizedFolder, UUID.randomUUID(), safeFilename);
    }

    private String normalizeFolder(String folder) {
        if (!StringUtils.hasText(folder)) {
            return "uploads";
        }
        return folder.replace("\\", "/").replaceAll("^/+", "").replaceAll("/+$", "");
    }
}
