package ru.tbank.springapp.configuration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
        @NotNull DataFetching dataFetching,
        @NotBlank String currencyConverterBaseUrl,
        @Positive Integer kudagoRateLimit
) {
    public record DataFetching(
            @NotNull Integer fetchingExecutorServiceThreadsNumber,
            @NotNull Duration period
    ) {
    }
}
