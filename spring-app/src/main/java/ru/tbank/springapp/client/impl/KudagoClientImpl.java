package ru.tbank.springapp.client.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.tbank.springapp.client.KudagoClient;
import ru.tbank.springapp.dto.CategoryDTO;
import ru.tbank.springapp.dto.CityDTO;
import ru.tbank.springapp.dto.events.kudago.EventListDTO;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class KudagoClientImpl implements KudagoClient {

    private final RestClient restClient;
    private final WebClient kudaGOWebClient;
    private final Semaphore kudagoSemaphore;

    @Override
    public List<CategoryDTO> getCategories() {
        try {
            kudagoSemaphore.acquire();

            log.info("Try to get all categories from Kudago API");

            return restClient
                    .get()
                    .uri("/place-categories/")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

        } catch (HttpServerErrorException e) {
            log.warn("Failed to fetch categories from Kudago API");
            return List.of();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            kudagoSemaphore.release();
        }
    }

    @Override
    public List<CityDTO> getCities() {
        try {
            kudagoSemaphore.acquire();

            log.info("Try to get all cities from Kudago API");

            return restClient
                    .get()
                    .uri("/locations/")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

        } catch (HttpServerErrorException e) {
            log.warn("Failed to fetch cities from Kudago API");
            return List.of();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            kudagoSemaphore.release();
        }
    }

    @Async
    @Override
    public CompletableFuture<EventListDTO> getEvents(LocalDate fromDate, LocalDate toDate) {
        try {
            if (! kudagoSemaphore.tryAcquire(1, TimeUnit.SECONDS)) {
                return CompletableFuture.completedFuture(new EventListDTO(0, null, null, List.of()));
            }

            log.info("Try to get events from Kudago API...");

            EventListDTO response = kudaGOWebClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/events/")
                            .queryParam("fields", "id,dates,title,description,price,is_free,site_url")
                            .queryParam("actual_since", fromDate.atStartOfDay().atOffset(ZoneOffset.UTC).toEpochSecond())
                            .queryParam("actual_until", toDate.atStartOfDay().plusDays(1).atOffset(ZoneOffset.UTC).toEpochSecond())
                            .queryParam("order_by", "-publication_date")
                            .queryParam("location", "spb")
                            .build()
                    )
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<EventListDTO>() {
                    })
                    .block();

            return CompletableFuture.completedFuture(response);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            kudagoSemaphore.release();
        }
    }

    @Override
    public Mono<EventListDTO> getEventsReactive(LocalDate fromDate, LocalDate toDate) {
        log.info("Try to get events from Kudago API reactively...");

        return kudaGOWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/events/")
                        .queryParam("fields", "id,dates,title,description,price,is_free,site_url")
                        .queryParam("actual_since", fromDate.atStartOfDay().atOffset(ZoneOffset.UTC).toEpochSecond())
                        .queryParam("actual_until", toDate.atStartOfDay().plusDays(1).atOffset(ZoneOffset.UTC).toEpochSecond())
                        .queryParam("order_by", "-publication_date")
                        .queryParam("location", "spb")
                        .build()
                )
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }

}
