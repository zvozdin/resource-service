package com.example.resourceservice.model;

public record ResourceModel(
        long instanceLength,
        long contentLength,
        byte[] content) {
}
