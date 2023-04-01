package com.example.resourceservice.service;

import com.example.resourceservice.model.ResourceModel;
import com.example.resourceservice.repository.entity.FileTrackingEntity;
import com.example.resourceservice.repository.FileTrackingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    public ResourceModel download(String id) {
        return fileStorageService.download(id);
    }

    public ResourceModel download(String id, long rangeStart, long rangeEnd) {
        return fileStorageService.download(id, rangeStart, rangeEnd);
    }

    public List<String> delete(List<String> ids) {
        return fileStorageService.delete(ids);
    }

}
