package ru.tbank.currencyapp.service.currency.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.currencyapp.client.CentralBankClient;
import ru.tbank.currencyapp.dto.CurrencyConvertedDTO;
import ru.tbank.currencyapp.dto.CurrencyRateDTO;
import ru.tbank.currencyapp.dto.cb.CBCurrencyResponseDTO;
import ru.tbank.currencyapp.exception.exceptions.CurrencyNotFoundException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


@ExtendWith(MockitoExtension.class)
class CurrencyServiceImplTest {

    @InjectMocks
    private CurrencyServiceImpl currencyService;

    @Mock
    private CentralBankClient centralBankClient;

    @BeforeEach
    void initCbClient() {
        Map<String, CBCurrencyResponseDTO> currencies = new HashMap<>();

        currencies.put("RUB", new CBCurrencyResponseDTO(1, "RUB", 1, "first", new BigDecimal("1.0"), new BigDecimal("1.0")));
        currencies.put("USD", new CBCurrencyResponseDTO(2, "USD", 2, "second", new BigDecimal("30.0"), new BigDecimal("30.0")));

        Mockito.when(centralBankClient.getCurrencies()).thenReturn(currencies);
    }

    @Test
    void givenCurrencyCode_whenGetRate_thenRateSuccessfullyRetrieved() {
        String code = "USD";
        BigDecimal expectedRate = new BigDecimal("30.0");

        CurrencyRateDTO rate = currencyService.getRate(code);

        Assertions.assertEquals(code, rate.currency());
        Assertions.assertEquals(expectedRate, rate.rate());
    }

    @Test
    void givenNonExistentCurrencyCode_whenGetRate_thenThrowCurrencyNotFoundException() {
        String code = "???";

        Assertions.assertThrows(CurrencyNotFoundException.class, () -> currencyService.getRate(code));
    }

    @Test
    void givenValidCurrenciesAndValidAmount_whenConvertCurrencies_thenSuccessfullyConverted() {
        String fromCurrency = "USD";
        String toCurrency = "RUB";
        BigDecimal amount = new BigDecimal("3.0");
        BigDecimal expected = new BigDecimal("90.000000");

        CurrencyConvertedDTO actual = currencyService.convertCurrencies(fromCurrency, toCurrency, amount);

        Assertions.assertEquals(fromCurrency, actual.fromCurrency());
        Assertions.assertEquals(toCurrency, actual.toCurrency());
        Assertions.assertEquals(expected, actual.convertedAmount());
    }

    @Test
    void givenNonExistentCurrencyCode_whenConvertCurrencies_thenThrowCurrencyNotFoundException() {
        String fromCurrency = "???";
        String toCurrency = "RUB";
        BigDecimal amount = new BigDecimal("3.0");

        Assertions.assertThrows(CurrencyNotFoundException.class, () -> currencyService.convertCurrencies(fromCurrency, toCurrency, amount));
    }

}