package ru.tbank.springapp.service;


import reactor.core.publisher.Mono;
import ru.tbank.springapp.dto.EventJpaDTO;
import ru.tbank.springapp.dto.events.EventResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface EventService {

    List<EventJpaDTO> findAll();

    EventJpaDTO findById(long id);

    EventJpaDTO create(LocalDate date, String name, String slug, String placeName);

    int update(long id, LocalDate date, String name, String slug, String placeName);

    long delete(long id);

    CompletableFuture<List<EventResponseDTO>> getAffordableEvents(BigDecimal budget, String currency, LocalDate fromDate, LocalDate toDate);

    Mono<List<EventResponseDTO>> getEventsReactive(BigDecimal budget, String currency, LocalDate fromDate, LocalDate toDate);

}
