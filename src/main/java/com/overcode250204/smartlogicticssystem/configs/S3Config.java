package com.overcode250204.smartlogicticssystem.configs;

import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    @Value("${aws.credentials.access-key}") String accessKey;
    @Value("${aws.credentials.secret-key}") String secretKey;
    @Value("${aws.region}") String region;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(software.amazon.awssdk.regions.Region.of(region))
                .credentialsProvider(software.amazon.awssdk.auth.credentials.StaticCredentialsProvider.create(
                        software.amazon.awssdk.auth.credentials.AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .build();
    }
}
