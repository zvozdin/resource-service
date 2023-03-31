package com.example.resourceservice.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.example.resourceservice.config.S3ClientConfig;
import com.example.resourceservice.exception.FileStorageServiceException;
import com.example.resourceservice.model.ResourceModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileStorageService {

    private final AmazonS3 amazonS3Client;
    private final S3ClientConfig s3Config;

    @PostConstruct
    public void initializeBucket() {
        if (!amazonS3Client.doesBucketExistV2(s3Config.getBucketName())) {
            amazonS3Client.createBucket(s3Config.getBucketName());
        }
    }

    public String upload(MultipartFile file) {
        String key = UUID.randomUUID().toString();
        try(InputStream input = file.getInputStream()) {
            amazonS3Client.putObject(s3Config.getBucketName(), key, input, buildObjectMetadata(file));
            return key;
        } catch (IOException e) {
            log.error("IO Exception happened during writing MultipartFile into to InputStream to put an object to S3");
            throw new FileStorageServiceException(e.getMessage());
        } catch (AmazonServiceException e) {
            String errorMessage = String.format(
                    "The call was transmitted successfully, but Amazon S3 couldn't process. AWS ErrorCode: %s, HttpCode: %d",
                    e.getErrorCode(), e.getStatusCode());
            log.error(errorMessage);
            throw new FileStorageServiceException(e.getErrorMessage());
        } catch (SdkClientException e) {
            String errorMessage = "Amazon S3 couldn't be contacted for a response or the client couldn't parse the response from Amazon S3";
            log.error(errorMessage);
            throw new FileStorageServiceException(e.getMessage());
        }
    }

    public ResourceModel getFile(String id) {
        return getResourceModel(new GetObjectRequest(s3Config.getBucketName(), id));
    }

    public ResourceModel getFile(String id, long rangeStart, long rangeEnd) {
        return getResourceModel(
                new GetObjectRequest(s3Config.getBucketName(), id)
                        .withRange(rangeStart, rangeEnd));
    }

    // todo: remove resource -> s3.deleteObject("bucket", "key");

    private ObjectMetadata buildObjectMetadata(MultipartFile file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        return objectMetadata;
    }

    private ResourceModel getResourceModel(GetObjectRequest objectRequest) {
        S3Object s3Object = amazonS3Client.getObject(objectRequest);
        long instanceLength = s3Object.getObjectMetadata().getInstanceLength();
        long contentLength = s3Object.getObjectMetadata().getContentLength();

        try(S3ObjectInputStream objectContent = s3Object.getObjectContent()) {
            byte[] content = IOUtils.toByteArray(objectContent);

            return new ResourceModel(instanceLength, contentLength, content);
        } catch (IOException e) {
            log.error("IO Exception happened during writing s3Object to byte[] result array");
            throw new FileStorageServiceException(e.getMessage());
        }
    }

}
