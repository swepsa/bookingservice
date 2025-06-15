package com.example.bookingsystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Configuration class that defines application-wide beans.
 */
@Configuration
public class AppConfig {

    /**
     * Creates and configures the OpenAPI documentation for the Booking System.
     *
     * @return the {@link OpenAPI} instance with title, version, and description
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Booking System API")
                        .version("1.0")
                        .description("API documentation for the Booking System"));
    }

    /**
     * Provides a system clock bean based on the default time zone.
     * Useful for injecting time source in services and makes testing easier by mocking the clock.
     *
     * @return the system {@link Clock}
     */
    @Bean
    public Clock systemClock() {
        return Clock.systemDefaultZone();
    }

    /**
     * Provides an {@link Executor} backed by Java virtual threads (Project Loom).
     * Allows for lightweight, scalable asynchronous task execution.
     *
     * @return an executor that creates a new virtual thread per task
     */
    @Bean
    public Executor virtualThreadExecutor() {
        return Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory());
    }
}


