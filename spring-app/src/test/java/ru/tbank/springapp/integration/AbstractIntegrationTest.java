package ru.tbank.springapp.integration;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tbank.springapp.WireMockTest;
import ru.tbank.springapp.configuration.ApplicationConfig;

import java.util.concurrent.Semaphore;

@Testcontainers
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.liquibase.enabled=true", "spring.main.allow-bean-definition-overriding=true"}
)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest extends WireMockTest {

    protected static PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("test-kudago-db")
            .withUsername("test-user")
            .withPassword("test-password");

    @DynamicPropertySource
    static void dataSourceInit(DynamicPropertyRegistry registry) {
        POSTGRES.start();
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @DynamicPropertySource
    static void liquibaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.liquibase.change-log", () -> "classpath:migrations/master.yml");
    }

    @TestConfiguration
    public static class ClientConfig {
        @Bean
        public RestClient restClient() {
            return RestClient.builder()
                    .baseUrl(wireMockExtension.baseUrl())
                    .build();
        }

        @Bean
        public WebClient kudaGOWebClient() {
            return WebClient.builder()
                    .baseUrl(wireMockExtension.baseUrl())
                    .build();
        }

        @Bean
        public WebClient currencyWebClient() {
            return WebClient.builder()
                    .baseUrl(wireMockExtension.baseUrl())
                    .build();
        }

        @Bean
        public Semaphore kudagoSemaphore(ApplicationConfig applicationConfig) {
            return new Semaphore(applicationConfig.kudagoRateLimit());
        }
    }

}
