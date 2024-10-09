package ru.tbank.currencyapp.client.impl;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import ru.tbank.currencyapp.client.CentralBankClient;
import ru.tbank.currencyapp.dto.cb.CBCurrencyResponseDTO;
import ru.tbank.currencyapp.exception.exceptions.ExternalSystemUnavailableException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(properties = {"spring.main.allow-bean-definition-overriding=true"})
@WireMockTest
class CentralBankClientImplTest {

    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Path cbResponseXmlPath = Path.of("src", "test", "resources", "cb_response.xml");

    @Autowired
    private WireMockServer server;

    @Autowired
    private CentralBankClient client;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void clearCache() {
        // Clear cache, otherwise second test won't be calling external system
        cacheManager.getCacheNames().forEach(name -> cacheManager.getCache(name).clear());
    }

    @Test
    void givenWorkingCentralBankApi_whenGetCurrencies_thenCurrenciesSuccessfullyRetrieved() throws IOException {
        server.stubFor(
                get(urlPathEqualTo("/XML_daily.asp")).withQueryParam("date_req", equalTo(LocalDate.now().format(DATE_TIME_FORMATTER)))
                        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_XML_VALUE))
                        .willReturn(
                                aResponse().withStatus(200).withHeader("Content-Type", MediaType.APPLICATION_XML_VALUE)
                                        .withBody(Files.readString(cbResponseXmlPath))
                        )
        );

        List<CBCurrencyResponseDTO> expectedCurrencies = List.of(
                new CBCurrencyResponseDTO(36, "AUD", 1, "Австралийский доллар", 16.0102, 16.0102),
                new CBCurrencyResponseDTO(826, "GBP", 1, "Фунт стерлингов Соединенного королевства", 43.8254, 43.8254)
        );

        List<CBCurrencyResponseDTO> actualCurrencies = client.getCurrencies();

        Assertions.assertIterableEquals(expectedCurrencies, actualCurrencies);
    }

    @Test
    void givenUnavailableCentralBankApi_whenGetCurrencies_thenHandlerMethodInvokeAndEmptyListReturns() {
        server.stubFor(
                any(anyUrl())
                        .willReturn(
                                aResponse().withStatus(503).withFault(Fault.CONNECTION_RESET_BY_PEER)
                        )
        );

        Assertions.assertThrows(ExternalSystemUnavailableException.class, () -> client.getCurrencies());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public WireMockServer wireMockServer() {
            WireMockServer server = new WireMockServer();
            server.start();
            return server;
        }

        @Bean
        public WebClient webClient(WireMockServer server) {
            return WebClient.builder()
                    .baseUrl(server.baseUrl())
                    .build();
        }
    }

}