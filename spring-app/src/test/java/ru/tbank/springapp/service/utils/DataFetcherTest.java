package ru.tbank.springapp.service.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.web.client.RestClient;
import ru.tbank.springapp.dao.Repository;
import ru.tbank.springapp.dao.jpa.PlaceRepository;
import ru.tbank.springapp.integration.AbstractIntegrationTest;
import ru.tbank.springapp.model.Category;
import ru.tbank.springapp.model.entities.PlaceEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@ExtendWith(MockitoExtension.class)
class DataFetcherTest extends AbstractIntegrationTest {

    @SpyBean
    private Repository<String, Category> categoryRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Test
    void testDataFetchingOnApplicationReadyEvent() {
        // Check that categories retrieved
        List<Category> categories = categoryRepository.getAll();
        assertTrue(categories.contains(new Category("fir", "first")));
        assertTrue(categories.contains(new Category("sec", "second")));

        // Check that places retrieved
        Optional<PlaceEntity> firstPlaceOptional = placeRepository.findBySlug("fir");
        assertTrue(firstPlaceOptional.isPresent());
        PlaceEntity firstPlace = firstPlaceOptional.get();
        assertEquals("first", firstPlace.getName());

        Optional<PlaceEntity> secondPlaceOptional = placeRepository.findBySlug("sec");
        assertTrue(secondPlaceOptional.isPresent());
        PlaceEntity secondPlace = secondPlaceOptional.get();
        assertEquals("second", secondPlace.getName());
    }

    @AfterEach
    void cleanDatabase() {
        placeRepository.deleteAll();
    }

    @TestConfiguration
    static class TestKudagoClientConfiguration {
        @Bean
        public RestClient restClient() {
            return RestClient.builder()
                    .baseUrl("http://localhost:" + wireMockExtension.getPort())
                    .build();
        }

        // To set up stubs for ApplicationReadyEvent
        // Because on ApplicationReadyEvent DataFetcher will start fetching places and categories with kudago client
        @EventListener(ApplicationStartedEvent.class)
        public void setUpStubs() throws IOException {
            Path pathToCategories = Path.of("src", "test", "resources", "client", "categories.json");
            String categoriesBody = Files.readString(pathToCategories);

            Path pathToCities = Path.of("src", "test", "resources", "client", "cities.json");
            String citiesBody = Files.readString(pathToCities);

            wireMockExtension.stubFor(
                    get("/place-categories/")
                            .willReturn(
                                    aResponse()
                                            .withStatus(200)
                                            .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                                            .withBody(categoriesBody)
                            )
            );

            wireMockExtension.stubFor(
                    get("/locations/")
                            .willReturn(
                                    aResponse()
                                            .withStatus(200)
                                            .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                                            .withBody(citiesBody)
                            )
            );
        }
    }

}