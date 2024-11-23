package ru.tbank.springapp.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.tbank.springapp.validation.validator.PasswordContsraintValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = PasswordContsraintValidator.class)
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {

    String message() default "Invalid password";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
