package ru.tbank.springapp.service.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.tbank.springapp.configuration.ApplicationConfig;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataFetchingScheduler {

    private final ScheduledExecutorService scheduledExecutorService;
    private final ApplicationConfig applicationConfig;
    private final DataFetcher dataFetcher;

    @EventListener(ApplicationStartedEvent.class)
    void scheduledDataFetching() {
        Duration period = applicationConfig.dataFetching().period();

        scheduledExecutorService.scheduleAtFixedRate(
                dataFetcher::fetchData,
                0,
                period.getSeconds(),
                TimeUnit.SECONDS
        );

        log.info("Data fetching scheduled with period: {}", period);
    }

}
