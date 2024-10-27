package ru.tbank.springapp.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tbank.springapp.dao.jpa.EventRepository;
import ru.tbank.springapp.dao.jpa.PlaceRepository;
import ru.tbank.springapp.dto.EventDTO;
import ru.tbank.springapp.exception.ResourceNotFoundException;
import ru.tbank.springapp.model.entities.EventEntity;
import ru.tbank.springapp.model.entities.PlaceEntity;
import ru.tbank.springapp.service.EventService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final PlaceRepository placeRepository;

    @Override
    public List<EventDTO> findAll() {
        log.info("Trying to get all events");
        return eventRepository
                .findAll()
                .stream()
                .map(EventDTO::fromEvent)
                .toList();
    }

    @Override
    public EventDTO findById(long id) {
        log.info("Trying to find event by id {}", id);

        EventEntity event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id " + id + " not found"));

        return EventDTO.fromEvent(event);
    }

    @Override
    public EventDTO create(LocalDate date, String name, String slug, String placeSlug) {
        log.info("Trying to create event with date = {}, name = {}, slug = {}, place slug = {}", date, name, slug, placeSlug);

        Optional<PlaceEntity> optionalPlace = placeRepository.findBySlug(placeSlug);

        if (optionalPlace.isEmpty()) {
            log.warn("Tried to create event with non-existent place slug {}", placeSlug);
            throw new ResourceNotFoundException(
                    String.format("Cannot create event with place %s, because place wasn't found", placeSlug)
            );
        }

        PlaceEntity place = optionalPlace.get();

        EventEntity eventEntity = new EventEntity();
        eventEntity.setPlace(place);
        eventEntity.setName(name);
        eventEntity.setSlug(slug);
        eventEntity.setStartDate(date);

        eventRepository.save(eventEntity);
        log.info("Event with id {} created", eventEntity.getId());

        return EventDTO.fromEvent(eventEntity);
    }

    @Override
    @Transactional
    public int update(int id, LocalDate date, String name, String slug, String placeSlug) {
        log.info("Trying to update event with id {}", id);

        Optional<PlaceEntity> optionalPlace = placeRepository.findBySlug(placeSlug);

        if (optionalPlace.isEmpty()) {
            log.warn("Tried to update event with non-existent place slug {}", placeSlug);
            throw new ResourceNotFoundException(
                    String.format("Cannot update event with place %s, because place wasn't found", placeSlug)
            );
        }

        PlaceEntity place = optionalPlace.get();

        int rowsAffected = eventRepository.updateById(id, date, name, slug, place.getId());

        if (rowsAffected == 0)
            throw new ResourceNotFoundException("Event with id " + id + " not found");

        log.info("Event with id {} updated", id);

        return rowsAffected;
    }

    @Override
    @Transactional
    public long delete(long id) {
        log.info("Trying to delete event with id {}", id);

        long rowsAffected = eventRepository.deleteById(id);

        if (rowsAffected == 0)
            throw new ResourceNotFoundException("Event with id " + id + " not found");

        log.info("Event with id {} deleted", id);

        return rowsAffected;
    }
}
