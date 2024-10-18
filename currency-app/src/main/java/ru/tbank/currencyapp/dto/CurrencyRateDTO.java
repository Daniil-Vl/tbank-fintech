package ru.tbank.currencyapp.dto;

import java.math.BigDecimal;

public record CurrencyRateDTO(
        String currency,
        BigDecimal rate
) {
}