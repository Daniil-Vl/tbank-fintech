package ru.tbank.springapp.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.tbank.springapp.client.CurrencyConverterClient;
import ru.tbank.springapp.client.KudagoClient;
import ru.tbank.springapp.dto.currency.CurrencyConvertedDTO;
import ru.tbank.springapp.dto.events.EventDTO;
import ru.tbank.springapp.dto.events.kudago.EventKudaGODTO;
import ru.tbank.springapp.dto.events.kudago.EventListDTO;
import ru.tbank.springapp.service.EventService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]+");
    private static final Pattern DO_PATTERN = Pattern.compile("до");

    private final CurrencyConverterClient currencyConverterClient;
    private final KudagoClient kudagoClient;

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
    public Flux<EventDTO> getEventsReactive(BigDecimal budget, String currency, LocalDate fromDate, LocalDate toDate) {
        Mono<EventListDTO> events = kudagoClient.getEventsReactive(fromDate, toDate);
        Mono<CurrencyConvertedDTO> rubConvertedDTO = currencyConverterClient.convertCurrencyReactively(currency, "RUB", budget);

        BigDecimal rubAmount = rubConvertedDTO.block().convertedAmount();

        return events
                .flatMapMany(eventListDTO -> Flux.fromIterable(eventListDTO.events()))
                .log()
                .filter(eventKudaGODTO -> canAffordEvent(rubAmount, eventKudaGODTO))
                .map(EventKudaGODTO::toEventDTO);
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
        if (! numMatcher.find())
            return false;

        int price = Integer.parseInt(numMatcher.group());
        int firstNumEnd = numMatcher.end();

        // If first number writes as "1 500 руб", then join this two numbers into one
        if (numMatcher.find() && numMatcher.start() - 1 == firstNumEnd) {
            price = price * 1000 + Integer.parseInt(numMatcher.group());
        }

        // When we didn't find "до"
        // Cases: "1500 руб", "от 1500 руб", "взрослый билет - 600 руб"
        if (! doMatcher.find()) {
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
