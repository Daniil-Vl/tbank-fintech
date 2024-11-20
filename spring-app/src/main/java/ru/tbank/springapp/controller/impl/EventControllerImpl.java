package ru.tbank.springapp.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.tbank.springapp.controller.EventController;
import ru.tbank.springapp.dto.EventJpaDTO;
import ru.tbank.springapp.dto.EventRequestDTO;
import ru.tbank.springapp.dto.events.EventResponseDTO;
import ru.tbank.springapp.service.EventService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class EventControllerImpl implements EventController {

    private final EventService eventService;

    @Override
    public List<EventJpaDTO> getJpaEvents() {
        return eventService.findAll();
    }

    @Override
    public EventJpaDTO getEventById(Long id) {
        return eventService.findById(id);
    }

    @Override
    public void createEvent(EventRequestDTO event) {
        eventService.create(
                event.date(),
                event.name(),
                event.slug(),
                event.placeSlug()
        );
    }

    @Override
    public void updateEvent(Long id, EventRequestDTO event) {
        eventService.update(
                id,
                event.date(),
                event.name(),
                event.slug(),
                event.placeSlug()
        );
    }

    @Override
    public void deleteEvent(Long id) {
        eventService.delete(id);
    }

    @Override
    public CompletableFuture<List<EventResponseDTO>> getEvents(BigDecimal budget, String currency, LocalDate fromDate, LocalDate toDate) {
        return eventService.getAffordableEvents(budget, currency, fromDate, toDate);
    }

    @Override
    public Mono<List<EventResponseDTO>> getEventsReactive(BigDecimal budget, String currency, LocalDate fromDate, LocalDate toDate) {
        return eventService.getEventsReactive(budget, currency, fromDate, toDate);
    }

}
