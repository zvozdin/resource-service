package com.example.resourceservice.cucumber.e2e;

import com.amazonaws.services.s3.AmazonS3;
import com.example.resourceservice.config.S3ClientConfig;
import com.example.resourceservice.repository.FileTrackingRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class E2ETestsStepDefinitions {

    @Autowired
    private HttpClient client;
    @Autowired
    private AmazonS3 amazonS3Client;
    @Autowired
    private S3ClientConfig s3ClientConfig;
    @Autowired
    private FileTrackingRepository fileTrackingRepository;

    @When("a client post file by {string} endpoint to store it into S3")
    public void aClientPostFileByApiVResourcesEndpointToStoreItIntoS(String path) {
        client.executePostFileToResourceService(path);
    }

    @Then("a client receives {int} as status code")
    public void aClientReceivesAsStatusCode(int statusCode) {
        assertSame(HttpStatus.valueOf(statusCode), client.getResponse().getStatusCode());
    }

    @And("a client receives resourceId to track it")
    public void aClientReceivesResourceIdToTrackIt() {
        assertThat(client.getResponse().getBody().id()).isNotBlank();
    }

    @And("a file has been stored into S3 with the same key as the resourceId from the response")
    public void aFileHasBeenStoredIntoSWithTheSameKeyAsTheResourceIdFromTheResponse() {
        assertTrue(amazonS3Client.doesObjectExist(s3ClientConfig.getBucketName(), client.getResponse().getBody().id()));
    }

    @And("a file tracking info - id and name have been stored into resource db")
    public void aFileTrackingInfoIdAndNameHaveBeenStoredIntoResourceDb() {
        assertThat(fileTrackingRepository.findByTrackingId(client.getResponse().getBody().id())).isPresent();
    }

    @And("a file metadata has been stored into song db")
    public void aFileMetadataHasBeenStoredIntoSongDb() {
        String expectedResourceId = client.getResponse().getBody().id();

        ResponseEntity<String> songServiceResponse =
                client.executeGetSongMetadataToSongService(expectedResourceId);

        assertThat(songServiceResponse.getStatusCodeValue()).isEqualTo(200);
    }

}
