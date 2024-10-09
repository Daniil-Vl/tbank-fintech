package ru.tbank.currencyapp.exception.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import ru.tbank.currencyapp.controller.CurrencyController;
import ru.tbank.currencyapp.dto.error.ApiErrorDTO;
import ru.tbank.currencyapp.exception.exceptions.CurrencyNotFoundException;
import ru.tbank.currencyapp.exception.exceptions.ExternalSystemUnavailableException;

import java.util.Arrays;

@Slf4j
@RestControllerAdvice(assignableTypes = CurrencyController.class)
public class CurrencyControllerExceptionHandler {

    @ExceptionHandler(value = HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorDTO handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        return handleValidationException(ex);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ApiErrorDTO handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return handleValidationException(ex);
    }

    /**
     * <p>Handles all exceptions that may be thrown when validating arguments</p>
     * <p>Spring can throw two types of exceptions</p>
     * <p>1) HandlerMethodValidationException</p>
     * <p>2) MethodArgumentNotValidException</p>
     *
     * More info - <a href="https://github.com/spring-projects/spring-framework/issues/31775">GitHub issue</a>
     *
     * @param ex - exception
     * @return api error response dto with code and reason message
     */
    private ApiErrorDTO handleValidationException(Exception ex) {
        log.warn("Controller parameter validation fails");
        log.warn(ex.getMessage());

        if (ex instanceof HandlerMethodValidationException handlerException) {
            return new ApiErrorDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    Arrays.stream(handlerException.getDetailMessageArguments()).toList().toString()
            );
        }

        MethodArgumentNotValidException methodArgException = (MethodArgumentNotValidException) ex;
        return new ApiErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                Arrays.stream(methodArgException.getDetailMessageArguments()).toList().toString()
        );
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ApiErrorDTO handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return new ApiErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );
    }

    @ExceptionHandler(value = CurrencyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private ApiErrorDTO handleCurrencyNotFoundException(CurrencyNotFoundException ex) {
        log.warn("Tried to get currency, that wasn't in central bank list");
        log.warn(ex.getMessage());
        return new ApiErrorDTO(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage()
        );
    }

    @ExceptionHandler(value = ExternalSystemUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    private ApiErrorDTO handleExternalSystemUnavailableException(ExternalSystemUnavailableException ex) {
        log.warn("Getting currencies from central bank api failed, because cb service unavailable...");
        log.warn(ex.getMessage());
        return new ApiErrorDTO(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                ex.getMessage()
        );
    }

}
