package ru.tbank.currencyapp.dto;

public record CurrencyRateDTO(
        String currency,
        Double rate
) {
}