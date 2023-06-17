package com.example.resourceservice.exception;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.MultiObjectDeleteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class GlobalResourceServiceExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        String message =
                e.getConstraintViolations().stream()
                        .map(ConstraintViolation::getMessage)
                        .findFirst().orElse("");

        return ResponseEntity.badRequest().body(message);
    }

    @ExceptionHandler(FileStorageServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleStorageServiceException(FileStorageServiceException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(AmazonS3Exception.class)
    public ResponseEntity<String> handleAmazonS3Exception(AmazonS3Exception e) {
        if (e.getStatusCode() == HttpStatus.NOT_FOUND.value() && e.getErrorCode().equals("NoSuchKey")) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(String.format("The resource with the specified id '%s' does not exist",
                            e.getAdditionalDetails().get("Key")));
        }
        return ResponseEntity.status(HttpStatus.valueOf(e.getStatusCode())).body(e.getMessage());
    }

    @ExceptionHandler(AmazonServiceException.class)
    public ResponseEntity<String> handleAmazonServiceException(AmazonServiceException e) {
        log.error("The call was transmitted successfully, but Amazon S3 couldn't process. AWS ErrorCode: {}, HttpCode: {}",
                e.getErrorCode(), e.getStatusCode());

        return ResponseEntity.status(HttpStatus.valueOf(e.getStatusCode())).body(e.getMessage());
    }

    @ExceptionHandler(SdkClientException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleSdkClientException(SdkClientException e) {
        log.error("Amazon S3 couldn't be contacted for a response or the client couldn't parse the response from Amazon S3");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(MultiObjectDeleteException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleMultiObjectDeleteException(MultiObjectDeleteException e) {
        log.error("Partial or total failure of the multi-object delete");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(String.format("ErrorCode %s, with errors list %s. Successfully deleted objects: %s",
                        e.getErrorCode(), e.getErrors(), e.getDeletedObjects()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
