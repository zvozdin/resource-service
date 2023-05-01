package com.example.resourceservice.cucumber.e2e;

import com.example.resourceservice.controller.entity.SavedResourceEntityResponse;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class HttpClient {

    private static final String SERVER_URL = "http://localhost";

    @Getter
    private ResponseEntity<SavedResourceEntityResponse> response = null;

    @Value("${services.url.song-service}")
    private String songServiceUrl;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    void executePostFileToResourceService(String path) {
        String url = String.format("%s:%s%s", SERVER_URL, port, path);

        RequestEntity<MultiValueMap<String, Object>> requestEntity = buildMultipartRequest(url);

        response = restTemplate.exchange(requestEntity, SavedResourceEntityResponse.class);
    }

    private RequestEntity<MultiValueMap<String, Object>> buildMultipartRequest(String url) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
            parameters.add("file", new FileSystemResource("src/test/resources/features/e2e/tam_u_topoli.mp3"));

            return RequestEntity.post(url)
                    .headers(headers)
                    .body(parameters);
    }

    @SneakyThrows
    public ResponseEntity<String> executeGetSongMetadataToSongService(String id) {
        RequestEntity<Void> songMetadataRequestEntity =
                RequestEntity
                        .get(UriComponentsBuilder.fromUriString(songServiceUrl + "/{id}")
                                .buildAndExpand(Map.of("id", id))
                                .toUri())
                        .build();

        Thread.sleep(3_000L);

        return restTemplate.exchange(songMetadataRequestEntity, String.class);
    }

}
