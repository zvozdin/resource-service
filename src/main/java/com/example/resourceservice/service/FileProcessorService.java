package com.example.resourceservice.service;

import com.example.resourceservice.client.GatewayClient;
import com.example.resourceservice.client.entity.StorageType;
import com.example.resourceservice.client.entity.StorageTypeResponse;
import com.example.resourceservice.model.ResourceModel;
import com.example.resourceservice.repository.entity.FileTrackingEntity;
import com.example.resourceservice.repository.FileTrackingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileProcessorService {

    private static final String NOT_FOUND_MESSAGE = "file with ID %s not found";

    private final FileStorageService fileStorageService;
    private final FileTrackingRepository fileTrackingRepository;
    private final GatewayClient gatewayClient;

    public String save(MultipartFile file) {
        String path = getStoragePathForType(StorageType.STAGING);

        String trackingId = UUID.randomUUID().toString();

        String key = fileStorageService.upload(file, trackingId, path);

        log.info("file has been stored in S3 with key {}", key);

        FileTrackingEntity fileTracking =
                new FileTrackingEntity(file.getOriginalFilename(), trackingId, key, StorageType.STAGING);

        fileTrackingRepository.save(fileTracking);

        log.info("fileTracking info has been stored into DB {}", fileTracking);

        return trackingId;
    }

    @Transactional(readOnly = true)
    public ResourceModel download(String id) {
        return fileStorageService.download(getResourceKey(id));
    }

    @Transactional(readOnly = true)
    public ResourceModel download(String id, long rangeStart, long rangeEnd) {
        return fileStorageService.download(getResourceKey(id), rangeStart, rangeEnd);
    }

    // todo: refactor to provide resource path to be able to find and delete in S3
    public List<String> delete(List<String> ids) {
        return fileStorageService.delete(ids);
    }

    @Transactional
    public String changeStorage(String trackingId) {
        String path = getStoragePathForType(StorageType.PERMANENT);

        fileTrackingRepository.findByTrackingId(trackingId)
                .ifPresentOrElse(
                        file -> {
                            String newDestination = fileStorageService.move(trackingId, file.getResourcePath(), path);
                            file.setResourcePath(newDestination);
                            file.setType(StorageType.PERMANENT);
                            // todo: update in DB. by @Transactional needs to be updated. check it
                        },
                        () -> {
                            throw new IllegalArgumentException(String.format(NOT_FOUND_MESSAGE, trackingId));
                        }
                );

        return trackingId;
    }

    // todo: add annotation @CircuitBreaker
    private String getStoragePathForType(StorageType type) {
        return gatewayClient.getStorageTypes().stream()
                .filter(storage -> storage.storageType() == type)
                .map(StorageTypeResponse::path)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Path for %s not found. Couldn't store a file", type)));
    }

    private String getResourceKey(String id) {
        return fileTrackingRepository.findByTrackingId(id)
                .map(FileTrackingEntity::getResourcePath)
                .orElseThrow(() -> new IllegalArgumentException(String.format(NOT_FOUND_MESSAGE, id)));
    }

}
