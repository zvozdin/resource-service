package com.example.resourceservice.controller;

import com.example.resourceservice.model.ResourceModel;
import com.example.resourceservice.service.FileProcessorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceControllerTest {

    private static final String RESOURCE_ID = "resourceId";
    private static final String RANGE = "bytes=0-1023";
    private static final int RANGE_START = 0;
    private static final int RANGE_END = 1023;

    @InjectMocks
    private ResourceController subject;

    @Mock
    private FileProcessorService fileProcessorService;
    @Mock
    private StreamBridge streamBridge;

    @Test
    void getResource_shouldCallFileProcessorServiceDownloadWhenRequestHasRange() {
        // given
        ResourceModel resourceModel = new ResourceModel(6000, 1024, new byte[0]);
        when(fileProcessorService.download(anyString(), anyLong(), anyLong())).thenReturn(resourceModel);

        // when
        subject.getResource(RANGE, RESOURCE_ID);

        // then
        verify(fileProcessorService, times(1)).download(RESOURCE_ID, RANGE_START, RANGE_END);
    }

    @Test
    void getResource_shouldCallFileProcessorServiceDownloadWhenRequestHasNoRange() {
        // given
        ResourceModel resourceModel = new ResourceModel(6000, 6000, new byte[0]);
        when(fileProcessorService.download(RESOURCE_ID)).thenReturn(resourceModel);

        // when
        subject.getResource(null, RESOURCE_ID);

        // then
        verify(fileProcessorService, times(1)).download(RESOURCE_ID);
    }

}
