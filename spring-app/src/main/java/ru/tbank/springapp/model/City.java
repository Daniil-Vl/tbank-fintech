package ru.tbank.springapp.model;

import ru.tbank.springapp.dto.CityDTO;

public record City(
        String slug,
        String name
) {
    public CityDTO toDTO() {
        return new CityDTO(slug, name);
    }
}
