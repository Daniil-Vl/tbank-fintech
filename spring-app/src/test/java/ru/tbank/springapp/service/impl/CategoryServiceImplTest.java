package ru.tbank.springapp.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.springapp.dao.Repository;
import ru.tbank.springapp.exception.ResourceNotFoundException;
import ru.tbank.springapp.model.Category;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private Repository<String, Category> repository;

    @InjectMocks
    private CategoryServiceImpl service;

    // FindAll
    @Test
    void givenOneCategory_whenFindAll_thenRepositoryGetAllCall() {
        service.findAll();
        verify(repository, times(1)).getAll();
    }

    // FindById
    @Test
    void givenOneCategory_whenFindById_thenReturnCategory() {
        Category existingCategory = new Category("fir", "first");

        when(
                repository.getById(eq(existingCategory.slug()))
        ).thenReturn(existingCategory);

        Category category = service.findById(existingCategory.slug());

        verify(repository, times(1)).getById(existingCategory.slug());
        assertEquals(existingCategory, category);
    }

    @Test
    void givenNewCategory_whenFindById_thenThrowResourceNotFoundException() {
        Category newCategory = new Category("thr", "third");

        when(
                repository.getById(eq(newCategory.slug()))
        ).thenReturn(
                null
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.findById(newCategory.slug())
        );
        verify(repository, times(1)).getById(newCategory.slug());
    }

    // Create
    @Test
    void givenNewCategory_whenCreate_thenSuccessfullySaved() {
        Category newCategory = new Category("thr", "third");

        when(
                repository.save(newCategory.slug(), newCategory)
        ).thenReturn(
                null
        );

        service.create(newCategory.slug(), newCategory.name());
        verify(repository, times(1)).save(newCategory.slug(), newCategory);
    }

    @Test
    void givenExistingCategory_whenCreate_thenUpdate() {
        Category modifiedCategory = new Category("fir", "second");

        service.create(modifiedCategory.slug(), modifiedCategory.name());
        verify(repository, times(1)).save(modifiedCategory.slug(), modifiedCategory);
    }

    // Update
    @Test
    void givenModifiedCategory_whenUpdate_thenSuccessfullyUpdate() {
        Category existingCategory = new Category("fir", "first");
        Category modifiedCategory = new Category("fir", "second");

        when(
                repository.update(modifiedCategory.slug(), modifiedCategory)
        ).thenReturn(
                existingCategory
        );

        service.update(modifiedCategory.slug(), modifiedCategory.name());
        verify(repository, times(1)).update(modifiedCategory.slug(), modifiedCategory);
    }

    @Test
    void givenNewCategory_whenUpdate_thenThrowResourceNotFoundException() {
        Category newCategory = new Category("thr", "third");

        when(
                repository.update(newCategory.slug(), newCategory)
        ).thenReturn(
                null
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.update(newCategory.slug(), newCategory.name())
        );
        verify(repository, times(1)).update(newCategory.slug(), newCategory);
    }

    // Delete
    @Test
    void givenExistingCategory_whenDelete_thenSuccessfullyDeleted() {
        Category existingCategory = new Category("fir", "first");

        when(
                repository.delete(existingCategory.slug())
        ).thenReturn(
                existingCategory
        );

        assertDoesNotThrow(() -> service.delete(existingCategory.slug()));
        verify(repository, times(1)).delete(existingCategory.slug());
    }

    @Test
    void givenNewCategory_whenDelete_thenThrowResourceNotFoundException() {
        Category newCategory = new Category("thr", "third");

        when(
                repository.delete(newCategory.slug())
        ).thenReturn(
                null
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.delete(newCategory.slug())
        );
        verify(repository, times(1)).delete(newCategory.slug());
    }
}