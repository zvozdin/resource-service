package com.example.resourceservice.controller;

import com.example.resourceservice.controller.entity.DeletedResourcesEntityResponse;
import com.example.resourceservice.controller.entity.SavedResourceEntityResponse;
import com.example.resourceservice.controller.entity.ValidFile;
import com.example.resourceservice.controller.entity.ValidList;
import com.example.resourceservice.model.ResourceModel;
import com.example.resourceservice.service.FileProcessorService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.function.Consumer;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/resources")
@RestController
public class ResourceController {

    public static final String AUDIO_MPEG_MEDIA_TYPE = "audio/mpeg";
    public static final int SINGLE_RANGE = 1;

    private final FileProcessorService fileProcessorService;
    private final StreamBridge streamBridge;

    @GetMapping(value = "/{id}", produces = AUDIO_MPEG_MEDIA_TYPE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> getResource(@RequestHeader(value = "Range", required = false) String range,
                                              @PathVariable String id) {
        if (HttpRange.parseRanges(range).size() == SINGLE_RANGE) {
            String[] ranges = range.substring("bytes=".length()).split("-");
            long rangeStart = Long.parseLong(ranges[0]);
            long rangeEnd = Long.parseLong(ranges[1]);

            ResourceModel resourceModel = fileProcessorService.download(id, rangeStart, rangeEnd);

            return ResponseEntity
                    .status(HttpStatus.PARTIAL_CONTENT)
                    .headers(httpHeadersConsumer(Long.toString(resourceModel.contentLength())))
                    .header(HttpHeaders.CONTENT_RANGE,
                            String.format("bytes %d-%d/%d", rangeStart, rangeEnd, resourceModel.instanceLength()))
                    .body(resourceModel.content());
        } else {
            ResourceModel resourceModel = fileProcessorService.download(id);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .headers(httpHeadersConsumer(Long.toString(resourceModel.contentLength())))
                    .body(resourceModel.content());
        }
    }

    @PostMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public HttpEntity<SavedResourceEntityResponse> storeResource(@ValidFile @RequestParam("file") MultipartFile file) {
        String id = fileProcessorService.save(file);

        streamBridge.send("producer-out-0", id);

        return new HttpEntity<>(new SavedResourceEntityResponse(id));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public HttpEntity<DeletedResourcesEntityResponse> deleteResource(@ValidList @RequestParam("id") List<String> ids) {
        return new HttpEntity<>(new DeletedResourcesEntityResponse(fileProcessorService.delete(ids)));
    }

    private Consumer<HttpHeaders> httpHeadersConsumer(String contentLength) {
        return httpHeaders -> {
            httpHeaders.add(HttpHeaders.CONTENT_LENGTH, contentLength);
            httpHeaders.add(HttpHeaders.CONTENT_TYPE, AUDIO_MPEG_MEDIA_TYPE);
            httpHeaders.add(HttpHeaders.ACCEPT_RANGES, "bytes");
        };
    }

}
