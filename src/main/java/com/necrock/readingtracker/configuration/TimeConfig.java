package com.necrock.readingtracker.configuration;

import com.necrock.readingtracker.configuration.exception.InvalidTimezoneException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.ZoneId;

@Configuration
public class TimeConfig {

    @Value("${app.timezone:UTC}")
    private String zoneId;

    @Bean
    public Clock clock(ZoneId zoneId) {
        return Clock.system(zoneId);
    }

    @Bean
    public ZoneId zoneId() {
        try {
            return ZoneId.of(zoneId);
        } catch (DateTimeException unused) {
            throw new InvalidTimezoneException(zoneId);
        }
    }
}
