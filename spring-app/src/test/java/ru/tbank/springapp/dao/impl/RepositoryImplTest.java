package ru.tbank.springapp.dao.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.tbank.springapp.dao.Repository;
import ru.tbank.springapp.model.Category;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryImplTest {

    private ConcurrentMap<String, Category> map;
    private Category existingCategory;
    private Repository<String, Category> repository;

    @BeforeEach
    void setUp() {
        map = new ConcurrentHashMap<>();

        existingCategory = new Category("fir", "first");
        map.put(existingCategory.slug(), existingCategory);

        repository = new RepositoryImpl<>(map);
    }

    @Test
    void givenOneCategory_whenGetAll_returnOneCategory() {
        List<Category> expected = List.of(existingCategory);

        List<Category> actual = repository.getAll();

        assertAll(
                () -> assertEquals(expected.size(), actual.size()),
                () -> assertIterableEquals(expected, actual)
        );
    }

    @Test
    void givenExistingCategory_whenGetById_thenReturnThisCategory() {
        Category expected = existingCategory;

        Category actual = repository.getById(existingCategory.slug());

        assertEquals(expected, actual);
    }

    @Test
    void givenNonExistingCategory_whenGetById_thenReturnNull() {
        Category actual = repository.getById("non-existing");

        assertNull(actual);
    }

    @Test
    void givenCategory_whenSave_thenSuccessfullySaved() {
        Category category = new Category("sec", "second");

        repository.save(category.slug(), category);

        Category savedCategory = repository.getById(category.slug());
        assertEquals(category, savedCategory);
    }

    @Test
    void givenExistingCategory_whenSave_thenUpdateCategory() {
        Category category = new Category("fir", "second");

        repository.save(category.slug(), category);

        List<Category> all = repository.getAll();
        assertAll(
                () -> assertEquals(1, all.size()),
                () -> assertEquals(category, all.getFirst())
        );
    }

    @Test
    void givenExistingCategory_whenUpdate_thenSuccessfullyUpdated() {
        Category category = new Category("fir", "second");

        repository.update(category.slug(), category);

        List<Category> all = repository.getAll();
        assertAll(
                () -> assertEquals(1, all.size()),
                () -> assertEquals(category, all.getFirst())
        );
    }

    @Test
    void givenNonExistingCategory_whenUpdate_thenDoNotUpdateAndReturnNull() {
        Category category = new Category("sec", "second");

        Category returnedValue = repository.update(category.slug(), category);

        List<Category> all = repository.getAll();
        Category foundedCategory = repository.getById(category.slug());
        assertAll(
                () -> assertEquals(1, all.size()),
                () -> assertNull(returnedValue),
                () -> assertNull(foundedCategory)
        );
    }

    @Test
    void givenExistingCategory_whenDelete_thenSuccessfullyDeleted() {
        Category category = existingCategory;

        Category returnedValue = repository.delete(category.slug());

        List<Category> all = repository.getAll();
        assertAll(
                () -> assertEquals(0, all.size()),
                () -> assertEquals(category, returnedValue)
        );
    }

    @Test
    void givenNonExistingCategory_whenDeleteById_thenReturnNull() {
        Category category = new Category("sec", "second");

        Category returnedValue = repository.delete(category.slug());

        List<Category> all = repository.getAll();
        assertAll(
                () -> assertEquals(1, all.size()),
                () -> assertEquals(existingCategory, all.getFirst()),
                () -> assertNull(returnedValue)
        );
    }
}