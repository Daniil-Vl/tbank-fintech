package ru.tbank.currencyapp.validation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.tbank.currencyapp.validation.validators.CurrencyValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {CurrencyValidator.class})
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrencyIsValid {
    String message() default "Currency is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
