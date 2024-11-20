package ru.tbank.springapp.client;

import reactor.core.publisher.Mono;
import ru.tbank.springapp.dto.CategoryDTO;
import ru.tbank.springapp.dto.PlaceDTO;
import ru.tbank.springapp.dto.events.kudago.EventListDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface KudagoClient {
    List<CategoryDTO> getCategories();

    List<PlaceDTO> getCities();

    CompletableFuture<EventListDTO> getEvents(LocalDate fromDate, LocalDate toDate);

    Mono<EventListDTO> getEventsReactive(LocalDate fromDate, LocalDate toDate);
}
