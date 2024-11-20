package ru.tbank.springapp.service;


import reactor.core.publisher.Mono;
import ru.tbank.springapp.dto.events.EventDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import ru.tbank.springapp.dto.EventDTO;

import java.time.LocalDate;
import java.util.List;

public interface EventService {

    List<EventDTO> findAll();

    EventDTO findById(long id);

    EventDTO create(LocalDate date, String name, String slug, String placeName);

    int update(int id, LocalDate date, String name, String slug, String placeName);

    long delete(long id);

    CompletableFuture<List<EventDTO>> getAffordableEvents(BigDecimal budget, String currency, LocalDate fromDate, LocalDate toDate);

    Mono<List<EventDTO>> getEventsReactive(BigDecimal budget, String currency, LocalDate fromDate, LocalDate toDate);

}
