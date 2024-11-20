package ru.tbank.springapp.model;

import ru.tbank.springapp.dto.PlaceDTO;

public record City(
        String slug,
        String name
) {
    public PlaceDTO toDTO() {
        return new PlaceDTO(slug, name);
    }
}
