package ru.tbank.springapp.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import ru.tbank.springapp.model.City;
import ru.tbank.springapp.model.entities.PlaceEntity;

public record PlaceDTO(
        @NotBlank @Length(max = 255) String slug,
        @NotBlank @Length(max = 255) String name
) {
    public static PlaceDTO fromPlaceEntity(PlaceEntity placeEntity) {
        return new PlaceDTO(placeEntity.getSlug(), placeEntity.getName());
    }
}
