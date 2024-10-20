package ru.tbank.currencyapp.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import ru.tbank.currencyapp.validation.annotations.CurrencyIsValid;

import java.util.Currency;

@Component
public class CurrencyValidator implements ConstraintValidator<CurrencyIsValid, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            Currency.getInstance(value);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
