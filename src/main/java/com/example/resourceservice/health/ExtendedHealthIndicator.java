package com.example.resourceservice.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

public interface ExtendedHealthIndicator extends HealthIndicator {

    Logger log = LoggerFactory.getLogger(ExtendedHealthIndicator.class);

    HealthIndicatorKey getIndicatorKey();

    void contributeToHealth();

    default Health health() {
        try {
            contributeToHealth();
            log.info("Health check of {} passed", getIndicatorKey());
        }
        catch (Exception exception) {
            log.error("Health check of {} has not passed", getIndicatorKey());
            return Health.down()
                    .withDetail("error", exception.getMessage())
                    .build();
        }
        return Health.up().build();
    }

}
