package ru.tbank.currencyapp.client.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.tbank.currencyapp.client.CentralBankClient;
import ru.tbank.currencyapp.dto.cb.CBCurrencyResponseDTO;
import ru.tbank.currencyapp.dto.cb.ValCursDTO;
import ru.tbank.currencyapp.exception.exceptions.ExternalSystemUnavailableException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CentralBankClientImpl implements CentralBankClient {

    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final WebClient webClient;

    @Override
    @CircuitBreaker(name = "central-bank-system", fallbackMethod = "handleGetCurrenciesError")
    @Cacheable("${app.central-bank-cache-name}")
    public List<CBCurrencyResponseDTO> getCurrencies() {
        log.info("Trying to get currencies info from central bank api...");

        ValCursDTO valCursDTO = webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/XML_daily.asp").queryParam("date_req", LocalDate.now().format(DATE_TIME_FORMATTER)).build())
                .accept(MediaType.APPLICATION_XML)
                .retrieve()
                .bodyToMono(ValCursDTO.class)
                .block();

        return valCursDTO.valuteDTOS.stream().map(CBCurrencyResponseDTO::fromValute).toList();
    }

    private List<CBCurrencyResponseDTO> handleGetCurrenciesError(Exception exc) {
        log.warn("Calling central bank system has failed...");
        log.warn("Exception: {}", exc.toString());
        throw new ExternalSystemUnavailableException("Central bank api unavailable");
    }

}
