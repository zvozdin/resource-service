package com.example.resourceservice.client.entity;

public record StorageTypeResponse(Long id, StorageType storageType, String bucket, String path) {
}
