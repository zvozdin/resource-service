package com.example.resourceservice.service;

import com.example.resourceservice.client.GatewayClient;
import com.example.resourceservice.client.entity.StorageType;
import com.example.resourceservice.client.entity.StorageTypeResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Retry(name = "storage-service-retry")
@CircuitBreaker(name = "storage-service-circuit-breaker", fallbackMethod = "fallback")
@Slf4j
@RequiredArgsConstructor
@Service
public class StorageDestinationService {

    private static Map<StorageType, String> storagePaths =
            Map.ofEntries(
                    Map.entry(StorageType.STAGING, "/pre-processed"),
                    Map.entry(StorageType.PERMANENT, "/processed")
            );

    private final GatewayClient gatewayClient;

    public String getStoragePathForType(StorageType type) {
        log.info("calling storage-service to retrieve storage paths");

        return gatewayClient.getStorageTypes().stream()
                .filter(storage -> storage.storageType() == type)
                .map(StorageTypeResponse::path)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Path for %s not found. Couldn't store a file", type)));
    }

    public String fallback(StorageType type, Exception exception) {
        String path = storagePaths.get(type);
        log.info("providing default value {} for StorageType {}", path, type);
        return path;
    }

}
