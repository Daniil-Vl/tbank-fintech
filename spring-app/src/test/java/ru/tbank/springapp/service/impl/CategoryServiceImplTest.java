package ru.tbank.springapp.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.tbank.springapp.dao.Repository;
import ru.tbank.springapp.exception.ResourceNotFoundException;
import ru.tbank.springapp.model.Category;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CategoryServiceImplTest {

    @Mock
    private Repository<String, Category> repository;

    @InjectMocks
    private CategoryServiceImpl service;

    private Category existingCategory;
    private Category modifiedCategory;
    private Category newCategory;

    @BeforeEach
    void setUp() {
        existingCategory = new Category("fir", "first");
        modifiedCategory = new Category("fir", "second");
        newCategory = new Category("thr", "third");

        // GetById
        when(
                repository.getById(eq(existingCategory.slug()))
        ).thenReturn(existingCategory);
        when(
                repository.getById(not(eq(existingCategory.slug())))
        ).thenReturn(null);

        // Save
        when(
                repository.save(modifiedCategory.slug(), modifiedCategory)
        ).thenReturn(existingCategory);
        when(
                repository.save(newCategory.slug(), newCategory)
        ).thenReturn(null);

        // Update
        when(
                repository.update(existingCategory.slug(), existingCategory)
        ).thenReturn(existingCategory);
        when(
                repository.update(modifiedCategory.slug(), modifiedCategory)
        ).thenReturn(existingCategory);
        when(
                repository.update(newCategory.slug(), newCategory)
        ).thenReturn(null);

        // Delete
        when(
                repository.delete(existingCategory.slug())
        ).thenReturn(existingCategory);
        when(
                repository.delete(newCategory.slug())
        ).thenReturn(null);
    }

    // FindAll
    @Test
    void givenOneCategory_whenFindAll_thenRepositoryGetAllCall() {
        service.findAll();
        verify(repository, times(1)).getAll();
    }

    // FindById
    @Test
    void givenOneCategory_whenFindById_thenReturnCategory() {
        Category category = service.findById(existingCategory.slug());

        verify(repository, times(1)).getById(existingCategory.slug());
        assertEquals(existingCategory, category);
    }

    @Test
    void givenNewCategory_whenFindById_thenThrowResourceNotFoundException() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> service.findById(newCategory.slug())
        );
        verify(repository, times(1)).getById(newCategory.slug());
    }

    // Create
    @Test
    void givenNewCategory_whenCreate_thenSuccessfullySaved() {
        service.create(newCategory.slug(), newCategory.name());
        verify(repository, times(1)).save(newCategory.slug(), newCategory);
    }

    @Test
    void givenExistingCategory_whenCreate_thenUpdate() {
        service.create(modifiedCategory.slug(), modifiedCategory.name());
        verify(repository, times(1)).save(modifiedCategory.slug(), modifiedCategory);
    }

    // Update
    @Test
    void givenModifiedCategory_whenUpdate_thenSuccessfullyUpdate() {
        service.update(modifiedCategory.slug(), modifiedCategory.name());
        verify(repository, times(1)).update(modifiedCategory.slug(), modifiedCategory);
    }

    @Test
    void givenNewCategory_whenUpdate_thenThrowResourceNotFoundException() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> service.update(newCategory.slug(), newCategory.name())
        );
        verify(repository, times(1)).update(newCategory.slug(), newCategory);
    }

    // Delete
    @Test
    void givenExistingCategory_whenDelete_thenSuccessfullyDeleted() {
        assertDoesNotThrow(() -> service.delete(existingCategory.slug()));
        verify(repository, times(1)).delete(existingCategory.slug());
    }

    @Test
    void givenNewCategory_whenDelete_thenThrowResourceNotFoundException() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> service.delete(newCategory.slug())
        );
        verify(repository, times(1)).delete(newCategory.slug());
    }
}