package ru.tbank.lesson_16.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tbank.lesson_16.dao.TestRepository;

@Service
@Slf4j
public class TestService {

    private final TestRepository testRepository;
    private final Timer messageSavingTimer;

    public TestService(TestRepository testRepository, MeterRegistry meterRegistry) {
        this.testRepository = testRepository;
        this.messageSavingTimer = meterRegistry.timer("message_saving_time");
    }

    public void saveMessage(String message) {
        log.info("Save message: {}", message);
        this.messageSavingTimer.record(() -> {
            try {
                testRepository.saveMessage(message);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
