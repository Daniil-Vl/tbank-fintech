package ru.tbank.currencyapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ru.tbank.currencyapp.validation.annotations.CurrencyIsValid;

public record CurrencyConvertRequestDTO(
        @NotBlank(message = "fromCurrency field can not be empty")
        @CurrencyIsValid
        String fromCurrency,

        @NotBlank(message = "toCurrency field can not be empty")
        @CurrencyIsValid
        String toCurrency,

        @NotNull(message = "amount field cannot be empty")
        @Positive(message = "amount must be non negative")
        Double amount
) {
}
