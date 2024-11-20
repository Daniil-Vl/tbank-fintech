package ru.tbank.springapp.dto.events;

public record EventResponseDTO(
        String title,
        String description,
        String price,
        String siteUrl
) {
}
