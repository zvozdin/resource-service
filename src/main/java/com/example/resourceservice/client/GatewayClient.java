package com.example.resourceservice.client;

import com.example.resourceservice.client.entity.StorageTypeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "gateway-service")
public interface GatewayClient {

    @GetMapping("/api/v1/storages")
    List<StorageTypeResponse> getStorageTypes();
}
