package ru.tbank.springapp.service;

import reactor.core.publisher.Flux;
import ru.tbank.springapp.dto.events.EventDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface EventService {

    CompletableFuture<List<EventDTO>> getAffordableEvents(BigDecimal budget, String currency, LocalDate fromDate, LocalDate toDate);

    Flux<EventDTO> getEventsReactive(BigDecimal budget, String currency, LocalDate fromDate, LocalDate toDate);
}
