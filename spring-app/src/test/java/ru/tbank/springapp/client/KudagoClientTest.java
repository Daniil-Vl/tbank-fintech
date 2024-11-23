package ru.tbank.springapp.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.wiremock.integrations.testcontainers.WireMockContainer;
import ru.tbank.springapp.client.impl.KudagoClientImpl;
import ru.tbank.springapp.dto.CategoryDTO;
import ru.tbank.springapp.dto.PlaceDTO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Testcontainers
class KudagoClientTest {

    @Container
    private static final WireMockContainer wireMockContainer = new WireMockContainer(
            DockerImageName.parse("wiremock/wiremock:2.35.0")
    );

    private static KudagoClient client;

    @BeforeAll
    static void setUp() {
        wireMockContainer.start();
        WireMock.configureFor(wireMockContainer.getHost(), wireMockContainer.getFirstMappedPort());

        RestClient restClient = RestClient.builder()
                .baseUrl(wireMockContainer.getBaseUrl())
                .build();

        client = new KudagoClientImpl(restClient);
    }

    @BeforeEach
    void setUpStubs() throws IOException {
        Path pathToCategories = Path.of("src", "test", "resources", "client", "categories.json");
        String categoriesBody = Files.readString(pathToCategories);

        Path pathToCities = Path.of("src", "test", "resources", "client", "cities.json");
        String citiesBody = Files.readString(pathToCities);

        stubFor(
                get("/place-categories/")
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                                        .withBody(categoriesBody)
                        )
        );

        stubFor(
                get("/locations/")
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                                        .withBody(citiesBody)
                        )
        );
    }

    @Test
    void givenClient_whenGetCategories_thenSuccessfullyGetAllCategories() {
        List<CategoryDTO> expectedCategories = List.of(
                new CategoryDTO("fir", "first"),
                new CategoryDTO("sec", "second")
        );

        List<CategoryDTO> categories = client.getCategories();

        Assertions.assertIterableEquals(expectedCategories, categories);
    }

    @Test
    void givenBrokenApi_whenGetCategories_thenReturnEmptyList() {
        stubFor(
                get("/place-categories/")
                        .willReturn(aResponse().withStatus(500))
        );

        List<CategoryDTO> categories = client.getCategories();

        Assertions.assertIterableEquals(List.of(), categories);
    }

    @Test
    void givenClient_whenGetCities_thenSuccessfullyGetAllCities() {
        List<PlaceDTO> expectedCities = List.of(
                new PlaceDTO("fir", "first"),
                new PlaceDTO("sec", "second")
        );

        List<PlaceDTO> cities = client.getCities();

        Assertions.assertIterableEquals(expectedCities, cities);
    }

    @Test
    void givenBrokenApi_whenGetCities_thenReturnEmptyList() {
        stubFor(
                get("/locations/")
                        .willReturn(aResponse().withStatus(500))
        );

        List<PlaceDTO> cities = client.getCities();

        Assertions.assertIterableEquals(List.of(), cities);
    }

}