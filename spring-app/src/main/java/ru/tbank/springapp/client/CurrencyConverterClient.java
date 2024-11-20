package ru.tbank.springapp.client;

import reactor.core.publisher.Mono;
import ru.tbank.springapp.dto.currency.CurrencyConvertedDTO;
import ru.tbank.springapp.dto.currency.CurrencyRateDTO;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

public interface CurrencyConverterClient {

    CompletableFuture<CurrencyRateDTO> getCurrencyToRubRate(String currency);

    CompletableFuture<CurrencyConvertedDTO> convertCurrency(String fromCurrency, String toCurrency, BigDecimal amount);

    Mono<CurrencyConvertedDTO> convertCurrencyReactively(String fromCurrency, String toCurrency, BigDecimal amount);

}
