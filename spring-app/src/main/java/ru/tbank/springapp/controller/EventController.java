package ru.tbank.springapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.tbank.springapp.dto.events.EventDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface EventController {

    @Operation(summary = "Получение событий на основе пользовательских пожеланий", operationId = "getEvents")
    @GetMapping("/events-async")
    CompletableFuture<List<EventDTO>> getEvents(
            @RequestParam BigDecimal budget,
            @RequestParam String currency, 
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate toDate
    );

    @Operation(summary = "Получение событий на основе пользовательских пожеланий", operationId = "getEvents")
    @GetMapping("/events-reactive")
    Mono<List<EventDTO>> getEventsReactive(
            @RequestParam BigDecimal budget,
            @RequestParam String currency,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate toDate
    ) throws InterruptedException;

}
