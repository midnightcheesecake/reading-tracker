package com.necrock.readingtracker.configuration;

import com.necrock.readingtracker.configuration.exception.InvalidTimezoneException;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;

import static com.necrock.readingtracker.configuration.AppPropertiesTestSupport.PROPERTY_APP_TIMEZONE;
import static com.necrock.readingtracker.configuration.AppPropertiesTestSupport.contextRunner;
import static com.necrock.readingtracker.configuration.AppPropertiesTestSupport.validProperties;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TimeConfigTest {

    @Test
    void timeConfig_withValidTimezone_createsExpectedZoneId() {
        var properties = validProperties();
        properties.put(PROPERTY_APP_TIMEZONE, "Europe/Brussels");

        var contextRunner = contextRunner(properties)
                .withUserConfiguration(TimeConfig.class);


        contextRunner.run(context -> {
            ZoneId zoneId = context.getBean(ZoneId.class);
            assertThat(zoneId.getId()).isEqualTo("Europe/Brussels");
        });
    }

    @Test
    void timeConfig_withInvalidTimezone_throwsException() {
        var properties = validProperties();
        properties.put(PROPERTY_APP_TIMEZONE, "Invalid/Zone");

        var contextRunner = contextRunner(properties)
                .withUserConfiguration(TimeConfig.class);

        contextRunner.run(context ->
                assertThat(context.getStartupFailure())
                        .hasRootCauseInstanceOf(InvalidTimezoneException.class));
    }

}