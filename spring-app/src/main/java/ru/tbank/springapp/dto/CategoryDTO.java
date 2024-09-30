package ru.tbank.springapp.dto;

import ru.tbank.springapp.model.Category;

public record CategoryDTO(
        String slug,
        String name
) {
    public Category toCategory() {
        return new Category(slug, name);
    }
}
