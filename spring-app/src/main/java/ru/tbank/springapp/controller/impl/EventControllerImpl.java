package ru.tbank.springapp.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.tbank.springapp.controller.EventController;
import ru.tbank.springapp.dto.events.EventDTO;
import ru.tbank.springapp.service.EventService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class EventControllerImpl implements EventController {

    private final EventService eventService;

    @Override
    public CompletableFuture<List<EventDTO>> getEvents(BigDecimal budget, String currency, LocalDate fromDate, LocalDate toDate) {
        return eventService.getAffordableEvents(budget, currency, fromDate, toDate);
    }

    @Override
    public Mono<List<EventDTO>> getEventsReactive(BigDecimal budget, String currency, LocalDate fromDate, LocalDate toDate) throws InterruptedException {
        return eventService.getEventsReactive(budget, currency, fromDate, toDate);
    }

}
