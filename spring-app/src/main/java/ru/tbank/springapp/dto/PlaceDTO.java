package ru.tbank.springapp.dto;

import jakarta.validation.constraints.NotBlank;
import ru.tbank.springapp.model.City;
import ru.tbank.springapp.model.entities.PlaceEntity;

public record PlaceDTO(
        @NotBlank String slug,
        @NotBlank String name
) {
    public static PlaceDTO fromPlaceEntity(PlaceEntity placeEntity) {
        return new PlaceDTO(placeEntity.getSlug(), placeEntity.getName());
    }

    public City toCity() {
        return new City(slug, name);
    }
}
