package com.example.resourceservice.controller.entity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public final class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {

    public static final String AUDIO_MPEG_MEDIA_TYPE = "audio/mpeg";

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
        return contentType != null && contentType.equals(AUDIO_MPEG_MEDIA_TYPE);
    }

}
