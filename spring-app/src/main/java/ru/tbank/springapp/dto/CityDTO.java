package ru.tbank.springapp.dto;

import ru.tbank.springapp.model.City;

public record CityDTO(
        String slug,
        String name
) {
    public City toCity() {
        return new City(slug, name);
    }
}
