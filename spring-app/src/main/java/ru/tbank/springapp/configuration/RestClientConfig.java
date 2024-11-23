package ru.tbank.springapp.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@Slf4j
public class RestClientConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl("https://kudago.com/public-api/v1.4")
                .build();
    }

}
