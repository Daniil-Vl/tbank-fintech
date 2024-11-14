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
import wiremock.com.google.common.collect.Maps;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

//import static com.github.tomakehurst.wiremock.client.WireMock.*;

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
                get(urlPathEqualTo("/")).withQueryParam("date_req", equalTo(LocalDate.now().format(DATE_TIME_FORMATTER)))
                        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_XML_VALUE))
                        .willReturn(
                                aResponse().withStatus(200).withHeader("Content-Type", MediaType.APPLICATION_XML_VALUE)
                                        .withBody(Files.readString(cbResponseXmlPath))
                        )
        );

        //
        Map<String, CBCurrencyResponseDTO> expectedCurrencies = Map.of(
                "AUD", new CBCurrencyResponseDTO(36, "AUD", 1, "Австралийский доллар", new BigDecimal("16.0102"), new BigDecimal("16.0102")),
                "GBP", new CBCurrencyResponseDTO(826, "GBP", 1, "Фунт стерлингов Соединенного королевства", new BigDecimal("43.8254"), new BigDecimal("43.8254")),
                "RUB", new CBCurrencyResponseDTO(1, "RUB", 1, "Russian Rubles", new BigDecimal("1.0"), new BigDecimal("1.0"))
        );

        Map<String, CBCurrencyResponseDTO> actualCurrencies = client.getCurrencies();

        Assertions.assertTrue(Maps.difference(expectedCurrencies, actualCurrencies).areEqual());
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
