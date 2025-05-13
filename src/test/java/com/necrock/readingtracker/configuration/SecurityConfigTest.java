package com.necrock.readingtracker.configuration;

import org.junit.jupiter.api.Test;

import java.security.Key;

import static com.necrock.readingtracker.testsupport.configuration.AppPropertiesTestSupport.contextRunner;
import static com.necrock.readingtracker.testsupport.configuration.AppPropertiesTestSupport.validProperties;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SecurityConfigTest {

    @Test
    void securityConfig_withJwtSigningKey_createsValidKey() {
        var properties = validProperties();

        var contextRunner = contextRunner(properties)
                .withUserConfiguration(SecurityConfig.class);

        contextRunner.run(context -> {
            Key key = context.getBean(Key.class);
            assertThat(key).isNotNull();
            assertThat(key.getAlgorithm()).isEqualTo("HmacSHA256");
        });
    }
}
