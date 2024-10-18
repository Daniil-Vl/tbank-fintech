package ru.tbank.currencyapp.dto;

import java.math.BigDecimal;

public record CurrencyConvertedDTO(
        String fromCurrency,
        String toCurrency,
        BigDecimal convertedAmount
) {
}
