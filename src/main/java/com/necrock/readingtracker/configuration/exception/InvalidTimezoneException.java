package com.necrock.readingtracker.configuration.exception;

public class InvalidTimezoneException extends RuntimeException {
    public InvalidTimezoneException(String zoneId) {
        super("Invalid time zone ID provided: " + zoneId + "'.\n"
                + "Please check the 'app.timezone' property in your application configuration file "
                + "(application.properties or application.yml).\n"
                + "Ensure that it is a valid time zone ID from the Java Time API, e.g., 'UTC', 'Europe/Amsterdam'.");
    }
}
