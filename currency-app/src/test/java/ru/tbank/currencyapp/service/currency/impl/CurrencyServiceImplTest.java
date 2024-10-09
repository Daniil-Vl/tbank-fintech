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

import java.util.ArrayList;
import java.util.List;


@ExtendWith(MockitoExtension.class)
class CurrencyServiceImplTest {

    @InjectMocks
    private CurrencyServiceImpl currencyService;

    @Mock
    private CentralBankClient centralBankClient;

    @BeforeEach
    void initCbClient() {
        List<CBCurrencyResponseDTO> currencies = new ArrayList<>();

        currencies.add(new CBCurrencyResponseDTO(1, "RUB", 1, "first", 1.0, 1.0));
        currencies.add(new CBCurrencyResponseDTO(2, "USD", 2, "second", 30.0, 30.0));

        Mockito.when(centralBankClient.getCurrencies()).thenReturn(currencies);
    }

    @Test
    void givenCurrencyCode_whenGetRate_thenRateSuccessfullyRetrieved() {
        String code = "USD";
        Double expected = 30.0;

        CurrencyRateDTO rate = currencyService.getRate(code);

        Assertions.assertEquals(code, rate.currency());
        Assertions.assertEquals(expected, rate.rate());
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
        Double amount = 3.0;
        Double expected = 90.0;

        CurrencyConvertedDTO actual = currencyService.convertCurrencies(fromCurrency, toCurrency, amount);

        Assertions.assertEquals(fromCurrency, actual.fromCurrency());
        Assertions.assertEquals(toCurrency, actual.toCurrency());
        Assertions.assertEquals(expected, actual.convertedAmount());
    }

    @Test
    void givenNonExistentCurrencyCode_whenConvertCurrencies_thenThrowCurrencyNotFoundException() {
        String fromCurrency = "???";
        String toCurrency = "RUB";
        Double amount = 3.0;

        Assertions.assertThrows(CurrencyNotFoundException.class, () -> currencyService.convertCurrencies(fromCurrency, toCurrency, amount));
    }

}