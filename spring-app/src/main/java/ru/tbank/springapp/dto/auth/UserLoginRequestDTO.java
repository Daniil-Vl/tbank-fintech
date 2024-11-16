package ru.tbank.springapp.dto.auth;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import ru.tbank.springapp.validation.annotation.ValidPassword;

public record UserLoginRequestDTO(
        @NotBlank
        @Length(max = 255)
        String username,

        @NotBlank
        @ValidPassword
        String password,

        boolean rememberMe
) {
}
