package ru.tbank.springapp.dto.currency;

import java.math.BigDecimal;

public record CurrencyRateDTO(
        String currency,
        BigDecimal rate
) {
}
