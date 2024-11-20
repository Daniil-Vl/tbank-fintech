package ru.tbank.currencyapp.service.currency;

import ru.tbank.currencyapp.dto.CurrencyConvertedDTO;
import ru.tbank.currencyapp.dto.CurrencyRateDTO;

import java.math.BigDecimal;

public interface CurrencyService {

    CurrencyRateDTO getRate(String code);

    CurrencyConvertedDTO convertCurrencies(String fromCurrency, String toCurrency, BigDecimal amount);

}
