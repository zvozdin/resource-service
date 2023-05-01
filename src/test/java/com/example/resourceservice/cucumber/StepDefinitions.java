package com.example.resourceservice.cucumber;

import com.example.resourceservice.controller.ResourceController;
import com.example.resourceservice.model.ResourceModel;
import com.example.resourceservice.service.FileStorageService;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class StepDefinitions {

    @Autowired
    private ResourceController resourceController;
    @Autowired
    private FileStorageService fileStorageService;

    private ResponseEntity<byte[]> response;

    @When("the client calls {string}")
    public void the_client_calls_to_get_all(String path) throws Throwable {
    }

    @Then("the client receives status code of {int}")
    public void the_client_receives_status_code_of(int statusCode) throws Throwable {
        when(fileStorageService.download(anyString()))
                .thenReturn(new ResourceModel(1024L, 1024L, "audio-content".getBytes()));

        response = resourceController.getResource("", "resource id");

        assertThat(response.getStatusCodeValue()).isEqualTo(statusCode);
    }

    @And("the client receives content as a byte array")
    public void theClientReceivesContentAsAByteArray() {
        assertThat(response.getBody()).isNotEmpty();
    }

}
