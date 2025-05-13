package com.necrock.readingtracker.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.HashMap;
import java.util.Map;

public class AppPropertiesTestSupport {
    public static final String PROPERTY_APP_TIMEZONE = "app.timezone";
    public static final String PROPERTY_APP_SIGNING_KEY = "app.signingKey";

    public static Map<String, String> validProperties() {
        var properties = new HashMap<String, String>();
        properties.put(PROPERTY_APP_TIMEZONE, "Europe/Brussels");
        properties.put(PROPERTY_APP_SIGNING_KEY, "uPZ0+3cEBqxq4FWcpDUl8STUj5pp8CjU3+pMZrsuFpE=");
        return properties;
    }

    public static ApplicationContextRunner contextRunner(Map<String, String> properties) {
        var propertiesArray = properties.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .toArray(String[]::new);

        return new ApplicationContextRunner()
                .withUserConfiguration(TestConfig.class)
                .withPropertyValues(propertiesArray);
    }

    @TestConfiguration
    @EnableConfigurationProperties(AppProperties.class)
    public static class TestConfig {}
}
