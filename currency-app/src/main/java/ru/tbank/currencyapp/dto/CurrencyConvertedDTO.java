package ru.tbank.currencyapp.dto;

public record CurrencyConvertedDTO(
        String fromCurrency,
        String toCurrency,
        Double convertedAmount
) {
}
