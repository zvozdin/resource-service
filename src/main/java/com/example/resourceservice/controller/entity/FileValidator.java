package com.example.resourceservice.controller.entity;

import com.example.resourceservice.controller.ResourceController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public final class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {

    @Override
    public void initialize(ValidFile constraintAnnotation) {
        log.info("File validator initialized.");
    }

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        log.info("Validating file");

        String contentType = value.getContentType();

        return isSupportedContentType(contentType);

    }

    private boolean isSupportedContentType(String contentType) {
        return contentType != null && contentType.equals(ResourceController.AUDIO_MPEG_MEDIA_TYPE);
    }

}
