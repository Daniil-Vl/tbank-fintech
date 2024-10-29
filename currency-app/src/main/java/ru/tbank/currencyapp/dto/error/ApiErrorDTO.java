package ru.tbank.currencyapp.dto.error;

public record ApiErrorDTO(
        Integer code,
        String message
) {
}
