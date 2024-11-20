package ru.tbank.springapp.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Semaphore;

@Configuration
@Slf4j
@Profile("!test")
public class ClientConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl("https://kudago.com/public-api/v1.4")
                .build();
    }

    @Bean
    public WebClient kudaGOWebClient() {
        return WebClient.builder()
                .baseUrl("https://kudago.com/public-api/v1.4")
                .build();
    }

    @Bean
    public WebClient currencyWebClient(ApplicationConfig applicationConfig) {
        return WebClient.builder()
                .baseUrl(applicationConfig.currencyConverterBaseUrl())
                .build();
    }

    @Bean
    public Semaphore kudagoSemaphore(ApplicationConfig applicationConfig) {
        return new Semaphore(applicationConfig.kudagoRateLimit());
    }

}
