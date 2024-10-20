package ru.tbank.springapp.client.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.tbank.springapp.client.CurrencyConverterClient;
import ru.tbank.springapp.dto.currency.CurrencyConvertRequestDTO;
import ru.tbank.springapp.dto.currency.CurrencyConvertedDTO;
import ru.tbank.springapp.dto.currency.CurrencyRateDTO;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrencyConverterClientImpl implements CurrencyConverterClient {

    private final WebClient currencyWebClient;

    @Async
    @Override
    public CompletableFuture<CurrencyRateDTO> getCurrencyToRubRate(String currency) {
        log.info("Trying to get currency rate {}...", currency);
        CurrencyRateDTO currencyRateDTO = currencyWebClient
                .get()
                .uri(uriBuilder -> uriBuilder.
                        path("/rates/{code}")
                        .build(currency)
                ).retrieve()
                .bodyToMono(CurrencyRateDTO.class)
                .block();
        return CompletableFuture.completedFuture(currencyRateDTO);
    }

    @Async
    @Override
    public CompletableFuture<CurrencyConvertedDTO> convertCurrency(String fromCurrency, String toCurrency, BigDecimal amount) {
        log.info("Trying to convert currency {} to {} with amount {}", fromCurrency, toCurrency, amount);

        CurrencyConvertRequestDTO body = new CurrencyConvertRequestDTO(fromCurrency, toCurrency, amount);

        CurrencyConvertedDTO convertedDTO = currencyWebClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/convert")
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(CurrencyConvertedDTO.class)
                .block();

        return CompletableFuture.completedFuture(convertedDTO);
    }

    @Override
    public Mono<CurrencyConvertedDTO> convertCurrencyReactively(String fromCurrency, String toCurrency, BigDecimal amount) {
        log.info("Trying to convert currency {} to {} with amount {} reactively", fromCurrency, toCurrency, amount);

        CurrencyConvertRequestDTO body = new CurrencyConvertRequestDTO(fromCurrency, toCurrency, amount);

        return currencyWebClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/convert")
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(CurrencyConvertedDTO.class);
    }

}
