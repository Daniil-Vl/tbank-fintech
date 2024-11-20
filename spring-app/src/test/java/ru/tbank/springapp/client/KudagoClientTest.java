package ru.tbank.springapp.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tbank.springapp.client.impl.KudagoClientImpl;
import ru.tbank.springapp.dto.CategoryDTO;
import ru.tbank.springapp.dto.PlaceDTO;
import ru.tbank.springapp.dto.events.kudago.EventKudaGODTO;
import ru.tbank.springapp.dto.events.kudago.EventListDTO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Testcontainers
class KudagoClientTest extends WireMockTest {

    private static final Path PATH_TO_CATEGORIES = Path.of("src", "test", "resources", "client", "categories.json");
    private static final Path PATH_TO_CITIES = Path.of("src", "test", "resources", "client", "cities.json");
    private static final Path PATH_TO_EVENTS = Path.of("src", "test", "resources", "client", "events.json");
    private static String CATEGORIES_BODY;
    private static String CITIES_BODY;
    private static String EVENTS_BODY;

    private static KudagoClient client;

    static void initClient() {
        RestClient restClient = RestClient.builder()
                .baseUrl(wireMockContainer.getBaseUrl())
                .build();

        WebClient webClient = WebClient.builder()
                .baseUrl(wireMockContainer.getBaseUrl())
                .build();

        Semaphore clientSemaphore = new Semaphore(2);

        client = new KudagoClientImpl(restClient, webClient, clientSemaphore);
    }

    static void initResponseBodies() throws IOException {
        CATEGORIES_BODY = Files.readString(PATH_TO_CATEGORIES);
        CITIES_BODY = Files.readString(PATH_TO_CITIES);
        EVENTS_BODY = Files.readString(PATH_TO_EVENTS);
    }

    @BeforeAll
    static void setUp() throws IOException {
        initServer();
        initClient();
        initResponseBodies();
    }

    private static @NotNull EventListDTO getEventListDTO(LocalDate fromDate, LocalDate toDate) {
        EventKudaGODTO first = new EventKudaGODTO(
                1,
                List.of(new EventKudaGODTO.Date(fromDate, toDate)),
                "first event",
                "first event description",
                "от 1000 руб",
                false,
                "first site url"
        );
        EventKudaGODTO second = new EventKudaGODTO(
                2,
                List.of(new EventKudaGODTO.Date(fromDate, toDate)),
                "second event",
                "second event description",
                "",
                true,
                "second site url"
        );
        return new EventListDTO(2, null, null, List.of(first, second));
    }

    @Test
    void givenClient_whenGetCategories_thenSuccessfullyGetAllCategories() {
        stubFor(
                get("/place-categories/")
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                        .withBody(CATEGORIES_BODY)
                        )
        );

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

        stubFor(
                get("/locations/")
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                        .withBody(CITIES_BODY)
                        )
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

    @Test
    void givenSlowApi_whenGetCategoriesInMultipleThreads_thenSemaphoreLimitsRequests() throws InterruptedException {
        WireMock.resetAllRequests();
        stubFor(
                get("/place-categories/")
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                        .withBody(CATEGORIES_BODY)
                                        .withFixedDelay(1000)
                        )
        );

        try (ExecutorService executorService = Executors.newFixedThreadPool(10)) {
            long startTime = System.nanoTime();

            for (int i = 0; i < 10; i++)
                executorService.submit(() -> client.getCategories());

            executorService.shutdown();
            boolean awaited = executorService.awaitTermination(6, TimeUnit.SECONDS);

            long endTime = System.nanoTime();

            assertTrue(endTime - startTime > Duration.ofSeconds(5).toNanos());
            assertTrue(awaited);
            verify(10, getRequestedFor(urlEqualTo("/place-categories/")));
        }
    }

    @Test
    void givenFromAndToDate_whenGetEventsAsync_thenSuccessfullyReturnEvents() throws ExecutionException, InterruptedException {
        LocalDate fromDate = LocalDateTime.ofEpochSecond(1000, 0, ZoneOffset.UTC).toLocalDate();
        LocalDate toDate = LocalDateTime.ofEpochSecond(2000, 0, ZoneOffset.UTC).toLocalDate();

        String fromDateString = String.valueOf(fromDate.atStartOfDay().atZone(ZoneOffset.UTC).toEpochSecond());
        String toDateString = String.valueOf(toDate.plusDays(1).atStartOfDay().atZone(ZoneOffset.UTC).toEpochSecond());

        EventListDTO expectedList = getEventListDTO(fromDate, toDate);

        stubFor(
                get(urlPathEqualTo("/events/"))
                        .withQueryParam("fields", equalTo("id,dates,title,description,price,is_free,site_url"))
                        .withQueryParam("actual_since", equalTo(fromDateString))
                        .withQueryParam("actual_until", equalTo(toDateString))
                        .withQueryParam("order_by", equalTo("-publication_date"))
                        .withQueryParam("location", equalTo("spb"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                        .withBody(EVENTS_BODY)
                        )
        );

        EventListDTO actualList = client.getEvents(fromDate, toDate).get();

        assertEquals(expectedList, actualList);
    }

    @Test
    void givenFromAndToDate_whenGetEventsReactive_thenSuccessfullyReturnEvents() {
        LocalDate fromDate = LocalDateTime.ofEpochSecond(1000, 0, ZoneOffset.UTC).toLocalDate();
        LocalDate toDate = LocalDateTime.ofEpochSecond(2000, 0, ZoneOffset.UTC).toLocalDate();

        String fromDateString = String.valueOf(fromDate.atStartOfDay().atZone(ZoneOffset.UTC).toEpochSecond());
        String toDateString = String.valueOf(toDate.plusDays(1).atStartOfDay().atZone(ZoneOffset.UTC).toEpochSecond());

        EventListDTO expectedList = getEventListDTO(fromDate, toDate);

        stubFor(
                get(urlPathEqualTo("/events/"))
                        .withQueryParam("fields", equalTo("id,dates,title,description,price,is_free,site_url"))
                        .withQueryParam("actual_since", equalTo(fromDateString))
                        .withQueryParam("actual_until", equalTo(toDateString))
                        .withQueryParam("order_by", equalTo("-publication_date"))
                        .withQueryParam("location", equalTo("spb"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                        .withBody(EVENTS_BODY)
                        )
        );

        EventListDTO actualList = client.getEventsReactive(fromDate, toDate).block();

        assertEquals(expectedList, actualList);
    }

}