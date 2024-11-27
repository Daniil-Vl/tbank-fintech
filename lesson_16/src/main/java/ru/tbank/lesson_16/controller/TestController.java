package ru.tbank.lesson_16.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.lesson_16.service.TestService;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping("/test")
    public String test() {
        String requestID = UUID.randomUUID().toString();
        try (var mdc = MDC.putCloseable("requestID", requestID)) {
            log.info("Requested test");
            return "test";
        }
    }

    @GetMapping("/save/{message}")
    public void save(
            @PathVariable String message
    ) {
        String requestID = UUID.randomUUID().toString();
        try (var mdc = MDC.putCloseable("requestID", requestID)) {
            log.info("Saving message: {}", message);
            testService.saveMessage(message);
        }
    }

}
