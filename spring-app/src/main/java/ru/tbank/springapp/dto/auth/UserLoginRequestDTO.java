package ru.tbank.springapp.dto.auth;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UserLoginRequestDTO(
        @NotBlank
        @Length(max = 255)
        String username,
        @NotBlank
        String password,
        boolean rememberMe
) {
}
