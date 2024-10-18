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

import java.math.BigDecimal;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final CentralBankClient client;

    public CurrencyRateDTO getRate(String code) {
        Map<String, CBCurrencyResponseDTO> currencies = client.getCurrencies();

        if (!currencies.containsKey(code))
            throw new CurrencyNotFoundException(String.format("Currency not found: %s", code));

        return new CurrencyRateDTO(code, currencies.get(code).vUnitRate());
    }

    public CurrencyConvertedDTO convertCurrencies(String fromCurrency, String toCurrency, BigDecimal amount) {
        Map<String, CBCurrencyResponseDTO> currencies = client.getCurrencies();

        if (!currencies.containsKey(fromCurrency))
            throw new CurrencyNotFoundException(String.format("From currency not found: %s", fromCurrency));

        if (!currencies.containsKey(toCurrency))
            throw new CurrencyNotFoundException(String.format("To currency not found: %s", toCurrency));

        CBCurrencyResponseDTO fromCurrencyDTO = currencies.get(fromCurrency);
        CBCurrencyResponseDTO toCurrencyDTO = currencies.get(toCurrency);

        BigDecimal rubAmount = fromCurrencyDTO.vUnitRate().multiply(amount);
        BigDecimal converted = rubAmount.divide(toCurrencyDTO.vUnitRate());

        return new CurrencyConvertedDTO(
                fromCurrency,
                toCurrency,
                converted
        );
    }

}
