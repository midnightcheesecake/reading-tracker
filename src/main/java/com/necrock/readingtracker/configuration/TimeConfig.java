package com.necrock.readingtracker.configuration;

import com.necrock.readingtracker.configuration.exception.InvalidTimezoneException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.ZoneId;

@Configuration
public class TimeConfig {

    private final AppProperties properties;

    public TimeConfig(AppProperties properties) {
        this.properties = properties;
    }

    @Bean
    public Clock clock(ZoneId zoneId) {
        return Clock.system(zoneId);
    }

    @Bean
    public ZoneId zoneId() {
        try {
            return ZoneId.of(properties.getTimezone());
        } catch (DateTimeException unused) {
            throw new InvalidTimezoneException(properties.getTimezone());
        }
    }
}
