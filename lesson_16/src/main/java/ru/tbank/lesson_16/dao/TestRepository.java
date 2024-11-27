package ru.tbank.lesson_16.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Random;

@Repository
@Slf4j
public class TestRepository {

    private static final Random RANDOM = new Random();

    public void saveMessage(String test) throws InterruptedException {
        log.info("Saving test: {}", test);

        // Simulate processing
        Thread.sleep(RANDOM.nextInt(100, 1000));
    }

}
