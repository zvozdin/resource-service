package com.example.resourceservice.health;

import com.example.resourceservice.repository.FileTrackingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ResourceDBHealthIndicator implements ExtendedHealthIndicator {

    private final FileTrackingRepository fileTrackingRepository;

    @Override
    public HealthIndicatorKey getIndicatorKey() {
        return HealthIndicatorKey.RESOURCE_DB;
    }

    @Override
    public void contributeToHealth() {
        fileTrackingRepository.count();
    }
}
