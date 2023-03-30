package com.example.resourceservice.controller;

import com.example.resourceservice.controller.entity.SavedResourceEntityResponse;
import com.example.resourceservice.controller.entity.ValidFile;
import com.example.resourceservice.service.FileProcessorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/resources")
@RestController
public class ResourceController {

    private final FileProcessorService fileProcessorService;

    // todo: CRUD operations for processing mp3 files.

    @PostMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public HttpEntity<SavedResourceEntityResponse> storeResource(@ValidFile @RequestParam("file") MultipartFile file) {
        return new HttpEntity<>(new SavedResourceEntityResponse(fileProcessorService.save(file)));
    }
}
