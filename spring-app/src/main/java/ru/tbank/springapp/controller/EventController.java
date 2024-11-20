package ru.tbank.springapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;
import ru.tbank.springapp.dto.EventJpaDTO;
import ru.tbank.springapp.dto.EventRequestDTO;
import ru.tbank.springapp.dto.events.EventResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface EventController {

    @GetMapping("/events")
    List<EventJpaDTO> getJpaEvents();

    @GetMapping("/events/{id}")
    EventJpaDTO getEventById(@PathVariable Long id);

    @PostMapping("/events")
    void createEvent(
            @RequestBody EventRequestDTO event
    );

    @PutMapping("/events/{id}")
    void updateEvent(
            @PathVariable Long id,
            @RequestBody EventRequestDTO event
    );

    @DeleteMapping("/events/{id}")
    void deleteEvent(
            @PathVariable Long id
    );

    @Operation(summary = "Получение событий на основе пользовательских пожеланий", operationId = "getEvents")
    @GetMapping("/events-async")
    CompletableFuture<List<EventResponseDTO>> getEvents(
            @RequestParam BigDecimal budget,
            @RequestParam String currency,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate toDate
    );

    @Operation(summary = "Получение событий на основе пользовательских пожеланий", operationId = "getEvents")
    @GetMapping("/events-reactive")
    Mono<List<EventResponseDTO>> getEventsReactive(
            @RequestParam BigDecimal budget,
            @RequestParam String currency,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate toDate
    ) throws InterruptedException;

}
