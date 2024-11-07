package ru.tbank.springapp.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequestDTO(
        @NotBlank
        String oldPassword,
        @NotBlank
        String newPassword
) {
}
