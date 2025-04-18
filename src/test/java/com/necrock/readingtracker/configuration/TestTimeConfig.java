package com.necrock.readingtracker.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@TestConfiguration
public class TestTimeConfig {

    public static final Instant NOW = Instant.parse("2020-01-01T00:00:00Z");

    @Bean
    @Primary
    public Clock testClock(ZoneId zoneId) {
        return Clock.fixed(NOW, zoneId);
    }

    @Bean
    @Primary
    public ZoneId testZoneId() {
        return ZoneId.of("UTC");
    }
}
