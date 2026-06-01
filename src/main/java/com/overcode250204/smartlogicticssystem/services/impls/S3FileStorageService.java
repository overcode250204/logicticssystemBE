package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.dtos.response.FileUploadResponseDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.PresignedUrlResponseDTO;
import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.FileStorageErrorCode;
import com.overcode250204.smartlogicticssystem.services.IFileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3FileStorageService implements IFileStorageService {
    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;
    private static final long PRESIGNED_URL_EXPIRES_IN_SECONDS = 300L;
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.receipt-bucket:}")
    private String receiptBucket;

    @Override
    public FileUploadResponseDTO uploadReceipt(MultipartFile file) {
        validateBucket();
        validateFile(file);
        String key = buildReceiptKey(file);

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(receiptBucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .serverSideEncryption(ServerSideEncryption.AES256)
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return FileUploadResponseDTO.builder()
                    .receiptImageKey(key)
                    .build();
        } catch (IOException e) {
            log.error("Cannot read receipt file before uploading to S3", e);
            throw new AppException(FileStorageErrorCode.FILE_UPLOAD_FAILED);
        } catch (S3Exception e) {
            log.error("S3 rejected receipt upload. bucket={}, status={}, awsErrorCode={}, message={}",
                    receiptBucket,
                    e.statusCode(),
                    e.awsErrorDetails() == null ? null : e.awsErrorDetails().errorCode(),
                    e.awsErrorDetails() == null ? e.getMessage() : e.awsErrorDetails().errorMessage(),
                    e);
            throw mapS3Exception(e);
        } catch (SdkClientException e) {
            log.error("S3 client cannot upload receipt. Check AWS credentials, region and network. bucket={}",
                    receiptBucket, e);
            throw new AppException(FileStorageErrorCode.S3_CONFIGURATION_INVALID);
        } catch (RuntimeException e) {
            log.error("Unexpected receipt upload failure. bucket={}", receiptBucket, e);
            throw new AppException(FileStorageErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public PresignedUrlResponseDTO createPresignedUrl(String key) {
        validateBucket();
        try {
            GetObjectPresignRequest request = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(PRESIGNED_URL_EXPIRES_IN_SECONDS))
                    .getObjectRequest(builder -> builder.bucket(receiptBucket).key(key))
                    .build();

            String url = s3Presigner.presignGetObject(request).url().toString();
            return PresignedUrlResponseDTO.builder()
                    .url(url)
                    .expiresInSeconds(PRESIGNED_URL_EXPIRES_IN_SECONDS)
                    .build();
        } catch (RuntimeException e) {
            log.error("Failed to create S3 presigned URL. bucket={}, key={}", receiptBucket, key, e);
            throw new AppException(FileStorageErrorCode.PRESIGNED_URL_FAILED);
        }
    }

    private AppException mapS3Exception(S3Exception e) {
        if (e.statusCode() == 403) {
            return new AppException(FileStorageErrorCode.S3_ACCESS_DENIED);
        }
        if (e.statusCode() == 404) {
            return new AppException(FileStorageErrorCode.S3_BUCKET_NOT_FOUND);
        }
        return new AppException(FileStorageErrorCode.FILE_UPLOAD_FAILED);
    }

    private void validateBucket() {
        if (!StringUtils.hasText(receiptBucket)) {
            throw new AppException(FileStorageErrorCode.BUCKET_NOT_CONFIGURED);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(FileStorageErrorCode.FILE_REQUIRED);
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new AppException(FileStorageErrorCode.INVALID_FILE_TYPE);
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new AppException(FileStorageErrorCode.FILE_TOO_LARGE);
        }
    }

    private String buildReceiptKey(MultipartFile file) {
        LocalDate now = LocalDate.now();
        String extension = switch (file.getContentType()) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> "";
        };
        return "receipts/%d/%02d/%s%s".formatted(now.getYear(), now.getMonthValue(), UUID.randomUUID(), extension);
    }
}
