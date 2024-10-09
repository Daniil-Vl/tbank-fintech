package ru.tbank.currencyapp.service.currency;

import ru.tbank.currencyapp.dto.CurrencyConvertedDTO;
import ru.tbank.currencyapp.dto.CurrencyRateDTO;

public interface CurrencyService {

    CurrencyRateDTO getRate(String code);

    CurrencyConvertedDTO convertCurrencies(String fromCurrency, String toCurrency, Double amount);

}
