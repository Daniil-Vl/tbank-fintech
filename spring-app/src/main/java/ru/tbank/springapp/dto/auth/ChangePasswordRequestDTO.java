package ru.tbank.springapp.dto.auth;

import jakarta.validation.constraints.NotBlank;
import ru.tbank.springapp.validation.annotation.ValidPassword;

public record ChangePasswordRequestDTO(
        @NotBlank
        @ValidPassword
        String oldPassword,

        @NotBlank
        @ValidPassword
        String newPassword
) {
}
