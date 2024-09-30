package ru.tbank.springapp.model;

import ru.tbank.springapp.dto.CategoryDTO;

public record Category(
        String slug,
        String name
) {
    public CategoryDTO toDTO() {
        return new CategoryDTO(slug, name);
    }
}
