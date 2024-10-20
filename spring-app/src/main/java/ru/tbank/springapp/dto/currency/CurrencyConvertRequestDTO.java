package ru.tbank.springapp.dto.currency;

import java.math.BigDecimal;

public record CurrencyConvertRequestDTO(
        String fromCurrency,
        String toCurrency,
        BigDecimal amount
) {
}
