package com.example.resourceservice.service;

import com.example.resourceservice.model.FileTrackingEntity;
import com.example.resourceservice.repository.FileTrackingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class FileProcessorService {

    private final FileStorageService fileStorageService;
    private final FileTrackingRepository fileTrackingRepository;

    public String save(MultipartFile file) {
        String trackingId = fileStorageService.upload(file);

        fileTrackingRepository.save(new FileTrackingEntity(file.getOriginalFilename(), trackingId));

        return trackingId;
    }

}
