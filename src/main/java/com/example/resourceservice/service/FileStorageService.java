package com.example.resourceservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
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
import java.util.List;
import java.util.stream.Collectors;

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

    public String upload(MultipartFile file, String id, String path) {
        String key = buildDestinationKey(id, path);
        try(InputStream input = file.getInputStream()) {
            amazonS3Client.putObject(s3Config.getBucketName(), key, input, buildObjectMetadata(file));
            return key;
        } catch (IOException e) {
            log.error("IO Exception happened during writing MultipartFile into to InputStream to put an object to S3");
            throw new FileStorageServiceException(e.getMessage());
        }
    }

    public ResourceModel download(String id) {
        return getResourceModel(new GetObjectRequest(s3Config.getBucketName(), id));
    }

    public ResourceModel download(String id, long rangeStart, long rangeEnd) {
        return getResourceModel(
                new GetObjectRequest(s3Config.getBucketName(), id)
                        .withRange(rangeStart, rangeEnd));
    }

    public List<String> delete(List<String> ids) {
        List<DeleteObjectsRequest.KeyVersion> keys =
                ids.stream()
                        .map(DeleteObjectsRequest.KeyVersion::new)
                        .collect(Collectors.toList());

        DeleteObjectsResult deleteObjectsResult =
                amazonS3Client.deleteObjects(
                        new DeleteObjectsRequest(s3Config.getBucketName())
                                .withKeys(keys));

        return deleteObjectsResult.getDeletedObjects().stream()
                .map(DeleteObjectsResult.DeletedObject::getKey)
                .toList();
    }

    public String move(String trackingId, String from, String to) {
        String bucketName = s3Config.getBucketName();
        String destinationKey = buildDestinationKey(trackingId, to);

        amazonS3Client.copyObject(new CopyObjectRequest(bucketName, from, bucketName, destinationKey));

        delete(List.of(from));

        return destinationKey;
    }

    private String buildDestinationKey(String trackingId, String to) {
        return String.format("%s/%s", to, trackingId);
    }

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

            log.info("Resource with id {} has been retrieved from S3", objectRequest.getKey());

            return new ResourceModel(instanceLength, contentLength, content);
        } catch (IOException e) {
            log.error("IO Exception happened during writing s3Object to byte[] result array");
            throw new FileStorageServiceException(e.getMessage());
        }
    }

}
