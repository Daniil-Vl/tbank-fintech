package ru.tbank.springapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record EventRequestDTO(
        @JsonFormat(pattern = "dd/MM/yyyy")
        @NotNull
        LocalDate date,
        @NotBlank String name,
        @NotBlank String slug,
        @NotBlank String placeSlug
) {
}
