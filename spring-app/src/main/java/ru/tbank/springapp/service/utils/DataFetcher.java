package ru.tbank.springapp.service.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.tbank.springapp.aspect.Timed;
import ru.tbank.springapp.patterns.observer.ClientPublisher;
import ru.tbank.springapp.patterns.observer.Subscriber;

@Service
@RequiredArgsConstructor
@Slf4j
class DataFetcher {

    private final ClientPublisher clientPublisher;
    private final Subscriber categoriesRepositorySubscriber;
    private final Subscriber citiesRepositorySubscriber;

    @EventListener(ApplicationStartedEvent.class)
    void makeSubscriptions() {
        clientPublisher.subscribe(categoriesRepositorySubscriber);
        clientPublisher.subscribe(citiesRepositorySubscriber);
    }

    @Timed
    @EventListener(ApplicationReadyEvent.class)
    void fetchData() {
        log.info("Fetching data from Kudago API...");
        clientPublisher.getCategories();
        clientPublisher.getCities();
    }

}
