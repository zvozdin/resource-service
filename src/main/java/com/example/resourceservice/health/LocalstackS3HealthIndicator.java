package com.example.resourceservice.health;

import com.amazonaws.services.s3.AmazonS3;
import com.example.resourceservice.config.S3ClientConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class LocalstackS3HealthIndicator implements ExtendedHealthIndicator {

    private final AmazonS3 amazonS3Client;
    private final S3ClientConfig s3Config;

    @Override
    public HealthIndicatorKey getIndicatorKey() {
        return HealthIndicatorKey.LOCALSTACK_S3;
    }

    @Override
    public void contributeToHealth() {
        amazonS3Client.doesBucketExistV2(s3Config.getBucketName());
    }
}
