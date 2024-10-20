package ru.tbank.springapp.dto.events;

public record EventDTO(
        String title,
        String description,
        String price,
        String siteUrl
) {
}
