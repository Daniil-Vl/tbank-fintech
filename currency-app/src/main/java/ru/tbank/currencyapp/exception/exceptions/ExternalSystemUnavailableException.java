package ru.tbank.currencyapp.exception.exceptions;

public class ExternalSystemUnavailableException extends RuntimeException {
    public ExternalSystemUnavailableException(String message) {
        super(message);
    }
}
