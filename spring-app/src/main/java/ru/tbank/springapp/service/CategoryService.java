package ru.tbank.springapp.service;

import ru.tbank.springapp.model.Category;

import java.util.List;

public interface CategoryService {
    List<Category> findAll();

    Category findById(String slug);

    Category create(String slug, String name);

    Category update(String slug, String name);

    Category delete(String slug);
}
