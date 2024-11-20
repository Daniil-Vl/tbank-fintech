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
import reactor.core.publisher.Mono;
import ru.tbank.springapp.client.CurrencyConverterClient;
import ru.tbank.springapp.client.KudagoClient;
import ru.tbank.springapp.dto.currency.CurrencyConvertedDTO;
import ru.tbank.springapp.dto.events.kudago.EventKudaGODTO;
import ru.tbank.springapp.dto.events.kudago.EventListDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
  
    private static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]+");
    private static final Pattern DO_PATTERN = Pattern.compile("до");

    private final EventRepository eventRepository;
    private final PlaceRepository placeRepository;
    private final CurrencyConverterClient currencyConverterClient;
    private final KudagoClient kudagoClient;

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
    @Transactional
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

        EventEntity eventEntity = EventEntity.builder()
                .place(place)
                .name(name)
                .slug(slug)
                .startDate(date)
                .build();

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

    @Override
    public CompletableFuture<List<EventDTO>> getAffordableEvents(BigDecimal budget, String currency, LocalDate fromDate, LocalDate toDate) {
        CompletableFuture<EventListDTO> events = kudagoClient.getEvents(fromDate, toDate);
        CompletableFuture<CurrencyConvertedDTO> rubConvertedDTO = currencyConverterClient.convertCurrency(currency, "RUB", budget);

        return events.thenCombine(
                rubConvertedDTO,
                (eventListDTO, rubBudget) -> findAffordableEvents(rubBudget.convertedAmount(), eventListDTO)
        );
    }

    @Override
    public Mono<List<EventDTO>> getEventsReactive(BigDecimal budget, String currency, LocalDate fromDate, LocalDate toDate) {
        Mono<EventListDTO> eventsMono = kudagoClient.getEventsReactive(fromDate, toDate);
        Mono<CurrencyConvertedDTO> convertedBudgetMono = currencyConverterClient.convertCurrencyReactively(currency, "RUB", budget);

        return eventsMono
                .zipWith(convertedBudgetMono)
                .map(tuple -> {
                    BigDecimal rubAmount = tuple.getT2().convertedAmount();
                    List<EventKudaGODTO> events = tuple.getT1().events();
                    return events
                            .stream()
                            .filter(eventKudaGODTO -> canAffordEvent(rubAmount, eventKudaGODTO))
                            .map(EventKudaGODTO::toEventDTO)
                            .toList();
                });
    }

    private List<EventDTO> findAffordableEvents(BigDecimal budget, EventListDTO events) {
        List<EventDTO> result = new ArrayList<>();

        for (var event : events.events())
            if (canAffordEvent(budget, event))
                result.add(event.toEventDTO());

        return result;
    }

    /**
     * Tries to parse event's price and checks, that person with given budget can afford this event
     *
     * @param budget - person's budget
     * @param event  - event to check
     * @return true, if person can afford event, otherwise false
     */
    private boolean canAffordEvent(BigDecimal budget, EventKudaGODTO event) {
        if (event.isFree())
            return true;

        Matcher numMatcher = NUMBER_PATTERN.matcher(event.price());
        Matcher doMatcher = DO_PATTERN.matcher(event.price());

        // Case when there are no numbers (skip event)
        if (!numMatcher.find())
            return false;

        int price = Integer.parseInt(numMatcher.group());
        int firstNumEnd = numMatcher.end();

        // If first number writes as "1 500 руб", then join this two numbers into one
        if (numMatcher.find() && numMatcher.start() - 1 == firstNumEnd) {
            price = price * 1000 + Integer.parseInt(numMatcher.group());
        }

        // When we didn't find "до"
        // Cases: "1500 руб", "от 1500 руб", "взрослый билет - 600 руб"
        if (!doMatcher.find()) {
            return budget.compareTo(new BigDecimal(price)) >= 0;
        }

        // When "до" after first num
        // Cases: "от 500 до 1500 руб"
        if (doMatcher.start() > numMatcher.end()) {
            return budget.compareTo(new BigDecimal(price)) >= 0;
        }

        // When "до" before first num
        // Cases: "до 1500 руб" and other unknown cases
        return false;
    }

}
