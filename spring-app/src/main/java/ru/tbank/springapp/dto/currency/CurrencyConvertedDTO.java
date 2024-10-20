package ru.tbank.springapp.dto.currency;

import java.math.BigDecimal;

public record CurrencyConvertedDTO(
        String fromCurrency,
        String toCurrency,
        BigDecimal convertedAmount
) {
}
