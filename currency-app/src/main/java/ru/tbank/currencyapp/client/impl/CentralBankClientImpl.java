package ru.tbank.currencyapp.client.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ru.tbank.currencyapp.client.CentralBankClient;
import ru.tbank.currencyapp.dto.cb.CBCurrencyResponseDTO;
import ru.tbank.currencyapp.dto.cb.ValCursDTO;
import ru.tbank.currencyapp.dto.cb.ValuteDTO;
import ru.tbank.currencyapp.exception.exceptions.ExternalSystemUnavailableException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CentralBankClientImpl implements CentralBankClient {

    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final static String DATE_PARAM_NAME = "date_req";
    private final WebClient webClient;

    private static CBCurrencyResponseDTO getRubDTO() {
        return new CBCurrencyResponseDTO(1, "RUB", 1, "Russian Rubles", new BigDecimal("1.0"), new BigDecimal("1.0"));
    }

    @Override
    @CircuitBreaker(name = "central-bank-system", fallbackMethod = "handleGetCurrenciesError")
    @Cacheable("${app.central-bank-cache-name}")
    public Map<String, CBCurrencyResponseDTO> getCurrencies() {
        log.info("Trying to get currencies info from central bank api...");

        ValCursDTO valCursDTO = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam(DATE_PARAM_NAME, LocalDate.now().format(DATE_TIME_FORMATTER))
                        .build()
                )
                .accept(MediaType.APPLICATION_XML)
                .retrieve()
                .bodyToMono(ValCursDTO.class)
                .block();

        Map<String, CBCurrencyResponseDTO> result = new HashMap<>();
        for (ValuteDTO valuteDTO : valCursDTO.valuteDTOS) {
            result.put(valuteDTO.charCode, CBCurrencyResponseDTO.fromValute(valuteDTO));
        }

        // Add rub valute info, for valute <-> rub conversion
        CBCurrencyResponseDTO rubDTO = getRubDTO();
        result.put(rubDTO.charCode(), rubDTO);

        return result;
    }

    private Map<String, CBCurrencyResponseDTO> handleGetCurrenciesError(Exception exc) {
        if (exc instanceof WebClientResponseException webClientResponseException) {
            log.warn("Web client exception: ");
            log.warn(webClientResponseException.getResponseBodyAsString());
        }

        log.warn("Calling central bank system has failed...");
        log.warn("Exception: {}", exc.toString());
        throw new ExternalSystemUnavailableException("Central bank api unavailable");
    }

}
