package ru.tbank.springapp.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.tbank.springapp.dto.EventDTO;
import ru.tbank.springapp.dto.EventRequestDTO;
import ru.tbank.springapp.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    List<EventDTO> getEvents() {
        return eventService.findAll();
    }

    @GetMapping("/{id}")
    EventDTO getEventById(
            @PathVariable @Positive int id
    ) {
        return eventService.findById(id);
    }

    @PostMapping
    void createEvent(
            @RequestBody @Valid EventRequestDTO eventRequestDTO
    ) {
        eventService.create(
                eventRequestDTO.date(),
                eventRequestDTO.name(),
                eventRequestDTO.slug(),
                eventRequestDTO.placeSlug()
        );
    }

    @PutMapping("/{id}")
    void updateEvent(
            @PathVariable @Positive int id,
            @RequestBody @Valid EventRequestDTO eventRequestDTO
    ) {
        eventService.update(
                id,
                eventRequestDTO.date(),
                eventRequestDTO.name(),
                eventRequestDTO.slug(),
                eventRequestDTO.placeSlug()
        );
    }

    @DeleteMapping("/{id}")
    void deleteEvent(
            @PathVariable @Positive long id
    ) {
        eventService.delete(id);
    }
}
