package com.overcode250204.smartlogicticssystem.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws.s3")
public record AwsS3Properties(
        String bucketName,
        String region,
        String accessKey,
        String secretKey
) {
}
