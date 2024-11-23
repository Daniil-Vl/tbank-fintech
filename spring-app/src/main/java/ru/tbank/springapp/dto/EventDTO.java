package ru.tbank.springapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import ru.tbank.springapp.model.entities.EventEntity;

import java.time.LocalDate;

public record EventDTO(
        @NotNull Long id,
        @NotNull LocalDate date,
        @NotBlank @Length(max = 300) String name,
        @NotBlank @Length(max = 255) String slug,
        @NotBlank String placeName
) {
    public static EventDTO fromEvent(EventEntity event) {
        return new EventDTO(
                event.getId(),
                event.getStartDate(),
                event.getName(),
                event.getSlug(),
                event.getPlace().getName()
        );
    }
}
