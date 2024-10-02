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
import ru.tbank.springapp.model.City;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CityServiceImplTest {

    @Mock
    private Repository<String, City> repository;

    @InjectMocks
    private CityServiceImpl service;

    private City existingCity;
    private City modifiedCity;
    private City newCity;

    @BeforeEach
    void setUp() {
        existingCity = new City("fir", "first");
        modifiedCity = new City("fir", "second");
        newCity = new City("thr", "third");

        // GetById
        when(
                repository.getById(eq(existingCity.slug()))
        ).thenReturn(existingCity);
        when(
                repository.getById(not(eq(existingCity.slug())))
        ).thenReturn(null);

        // Save
        when(
                repository.save(modifiedCity.slug(), modifiedCity)
        ).thenReturn(existingCity);
        when(
                repository.save(newCity.slug(), newCity)
        ).thenReturn(null);

        // Update
        when(
                repository.update(existingCity.slug(), existingCity)
        ).thenReturn(existingCity);
        when(
                repository.update(modifiedCity.slug(), modifiedCity)
        ).thenReturn(existingCity);
        when(
                repository.update(newCity.slug(), newCity)
        ).thenReturn(null);

        // Delete
        when(
                repository.delete(existingCity.slug())
        ).thenReturn(existingCity);
        when(
                repository.delete(newCity.slug())
        ).thenReturn(null);
    }

    // FindAll
    @Test
    void givenOneCity_whenFindAll_thenRepositoryGetAllCall() {
        service.findAll();
        verify(repository, times(1)).getAll();
    }

    // FindById
    @Test
    void givenOneCity_whenFindById_thenReturnCity() {
        City category = service.findById(existingCity.slug());

        verify(repository, times(1)).getById(existingCity.slug());
        assertEquals(existingCity, category);
    }

    @Test
    void givenNewCity_whenFindById_thenThrowResourceNotFoundException() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> service.findById(newCity.slug())
        );
        verify(repository, times(1)).getById(newCity.slug());
    }

    // Create
    @Test
    void givenNewCity_whenCreate_thenSuccessfullySaved() {
        service.create(newCity.slug(), newCity.name());
        verify(repository, times(1)).save(newCity.slug(), newCity);
    }

    @Test
    void givenExistingCity_whenCreate_thenUpdate() {
        service.create(modifiedCity.slug(), modifiedCity.name());
        verify(repository, times(1)).save(modifiedCity.slug(), modifiedCity);
    }

    // Update
    @Test
    void givenModifiedCity_whenUpdate_thenSuccessfullyUpdate() {
        service.update(modifiedCity.slug(), modifiedCity.name());
        verify(repository, times(1)).update(modifiedCity.slug(), modifiedCity);
    }

    @Test
    void givenNewCity_whenUpdate_thenThrowResourceNotFoundException() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> service.update(newCity.slug(), newCity.name())
        );
        verify(repository, times(1)).update(newCity.slug(), newCity);
    }

    // Delete
    @Test
    void givenExistingCity_whenDelete_thenSuccessfullyDeleted() {
        assertDoesNotThrow(() -> service.delete(existingCity.slug()));
        verify(repository, times(1)).delete(existingCity.slug());
    }

    @Test
    void givenNewCity_whenDelete_thenThrowResourceNotFoundException() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> service.delete(newCity.slug())
        );
        verify(repository, times(1)).delete(newCity.slug());
    }
}