package ru.tbank.currencyapp.service.currency.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tbank.currencyapp.client.CentralBankClient;
import ru.tbank.currencyapp.dto.CurrencyConvertedDTO;
import ru.tbank.currencyapp.dto.CurrencyRateDTO;
import ru.tbank.currencyapp.dto.cb.CBCurrencyResponseDTO;
import ru.tbank.currencyapp.exception.exceptions.CurrencyNotFoundException;
import ru.tbank.currencyapp.service.currency.CurrencyService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final CentralBankClient client;

    public CurrencyRateDTO getRate(String code) {
        List<CBCurrencyResponseDTO> currencies = client.getCurrencies();

        for (var currency : currencies) {
            if (currency.charCode().equals(code)) {
                return new CurrencyRateDTO(code, currency.vUnitRate());
            }
        }

        throw new CurrencyNotFoundException(String.format("Currency not found: %s", code));
    }

    public CurrencyConvertedDTO convertCurrencies(String fromCurrency, String toCurrency, Double amount) {
        List<CBCurrencyResponseDTO> currencies = client.getCurrencies();

        CBCurrencyResponseDTO fromCurrencyDTO = null;
        CBCurrencyResponseDTO toCurrencyDTO = null;

        for (var currency : currencies) {
            if (currency.charCode().equals(fromCurrency))
                fromCurrencyDTO = currency;
            if (currency.charCode().equals(toCurrency))
                toCurrencyDTO = currency;
        }

        if (fromCurrencyDTO == null)
            throw new CurrencyNotFoundException(String.format("From currency not found: %s", fromCurrency));
        if (toCurrencyDTO == null)
            throw new CurrencyNotFoundException(String.format("To currency not found: %s", toCurrency));

        Double rubAmount = fromCurrencyDTO.value() * amount;

        return new CurrencyConvertedDTO(
                fromCurrency,
                toCurrency,
                rubAmount / toCurrencyDTO.value()
        );
    }

}
