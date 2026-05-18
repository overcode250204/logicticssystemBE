package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.FileErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

import static software.amazon.awssdk.core.sync.RequestBody.fromBytes;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class S3Service {

    S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    @NonFinal
    String bucketName;

    public String uploadFile(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new AppException(FileErrorCode.EMPTY_FILE);
        }

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".csv")) {
            throw new AppException(FileErrorCode.INVALID_CSV_FILE);
        }

        String fileName = "csv/" + UUID.randomUUID() + "-" + originalFilename;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType("text/csv")
                .contentLength(file.getSize())
                .build();

        s3Client.putObject(
                putObjectRequest,
                fromBytes(file.getBytes())
        );

        return fileName;
    }
}