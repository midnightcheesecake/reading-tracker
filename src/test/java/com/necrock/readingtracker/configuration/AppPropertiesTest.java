package com.necrock.readingtracker.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.validation.BindValidationException;

import static com.necrock.readingtracker.testsupport.configuration.AppPropertiesTestSupport.PROPERTY_APP_SIGNING_KEY;
import static com.necrock.readingtracker.testsupport.configuration.AppPropertiesTestSupport.PROPERTY_APP_TIMEZONE;
import static com.necrock.readingtracker.testsupport.configuration.AppPropertiesTestSupport.contextRunner;
import static com.necrock.readingtracker.testsupport.configuration.AppPropertiesTestSupport.validProperties;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AppPropertiesTest {

    @Test
    void appProperties_withCustomTimezone_acceptsCustomTimezone() {
        String timezone = "Europe/Brussels";
        var properties = validProperties();
        properties.put(PROPERTY_APP_TIMEZONE, timezone);

        var contextRunner = contextRunner(properties);

        contextRunner.run(context -> {
            var appProperties = context.getBean(AppProperties.class);
            assertThat(appProperties.getTimezone()).isEqualTo(timezone);
        });
    }

    @Test
    void appProperties_withMissingTimezone_setsDefaultTimezone() {
        var properties = validProperties();
        properties.remove(PROPERTY_APP_TIMEZONE);

        var contextRunner = contextRunner(properties);

        contextRunner.run(context -> {
            var appProperties = context.getBean(AppProperties.class);
            assertThat(appProperties.getTimezone()).isEqualTo("UTC");
        });
    }

    @Test
    void appProperties_acceptsSigningKey() {
        var signingKey = "somebase64key==";
        var properties = validProperties();
        properties.put(PROPERTY_APP_SIGNING_KEY, signingKey);

        var contextRunner = contextRunner(properties);

        contextRunner.run(context -> {
            var appProperties = context.getBean(AppProperties.class);
            assertThat(appProperties.getSigningKey()).isEqualTo(signingKey);
        });
    }

    @Test
    void appProperties_withMissingSigningKey_throwsValidationError() {
        var properties = validProperties();
        properties.remove(PROPERTY_APP_SIGNING_KEY);

        var contextRunner = contextRunner(properties);

        contextRunner.run(context ->
                assertThat(context.getStartupFailure())
                        .hasRootCauseInstanceOf(BindValidationException.class));
    }
}
