package com.example.resourceservice.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.example.resourceservice.config.S3ClientConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(
        properties = {"spring.main.allow-bean-definition-overriding=true"},
        classes = {
                FileStorageService.class,
                AmazonS3.class,
                S3ClientConfig.class,
                FileStorageServiceIT.AwsTestConfig.class
        }
)
class FileStorageServiceIT {

    private static final DockerImageName localstackImage =
            DockerImageName.parse("localstack/localstack:0.11.3");

    @Container
    public static LocalStackContainer localstackS3Container =
            new LocalStackContainer(localstackImage).withServices(LocalStackContainer.Service.S3);

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private AmazonS3 amazonS3Client;
    @Autowired
    private S3ClientConfig s3ClientConfig;

    @Test
    void upload() {
        // given
        MultipartFile file = new MockMultipartFile("test-file", "test-content".getBytes());

        // when
        String expectedKey = fileStorageService.upload(file, "/files", "path");

        // then
        S3Object actualStoredObject = amazonS3Client.getObject(new GetObjectRequest(s3ClientConfig.getBucketName(), expectedKey));

        assertThat(actualStoredObject.getKey()).isEqualTo(expectedKey);
    }

    @TestConfiguration
    static class AwsTestConfig {

        @Bean
        public AmazonS3 amazonS3() {
            return AmazonS3ClientBuilder
                    .standard()
                    .withEndpointConfiguration(
                            new AwsClientBuilder.EndpointConfiguration(
                                    localstackS3Container.getEndpointOverride(LocalStackContainer.Service.S3).toString(),
                                    localstackS3Container.getRegion()))
                    .withCredentials(
                            new AWSStaticCredentialsProvider(
                                    new BasicAWSCredentials(
                                            localstackS3Container.getAccessKey(), localstackS3Container.getSecretKey())))
                    .build();
        }

    }

}
