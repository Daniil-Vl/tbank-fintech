package ru.tbank.currencyapp.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
        @NotBlank
        String centralBankCurrenciesUrl,
        @NotBlank
        String centralBankCacheName
) {
}
