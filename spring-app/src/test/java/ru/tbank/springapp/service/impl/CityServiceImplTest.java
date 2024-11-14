package ru.tbank.springapp.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.springapp.dao.Repository;
import ru.tbank.springapp.exception.ResourceNotFoundException;
import ru.tbank.springapp.model.City;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CityServiceImplTest {

    @Mock
    private Repository<String, City> repository;

    @InjectMocks
    private CityServiceImpl service;

    // FindAll
    @Test
    void givenOneCity_whenFindAll_thenRepositoryGetAllCall() {
        service.findAll();
        verify(repository, times(1)).getAll();
    }

    // FindById
    @Test
    void givenOneCity_whenFindById_thenReturnCity() {
        City existingCity = new City("fir", "first");

        when(
                repository.getById(eq(existingCity.slug()))
        ).thenReturn(
                existingCity
        );

        City city = service.findById(existingCity.slug());

        verify(repository, times(1)).getById(existingCity.slug());
        assertEquals(existingCity, city);
    }

    @Test
    void givenNewCity_whenFindById_thenThrowResourceNotFoundException() {
        City newCity = new City("thr", "third");

        when(
                repository.getById(eq(newCity.slug()))
        ).thenReturn(
                null
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.findById(newCity.slug())
        );
        verify(repository, times(1)).getById(newCity.slug());
    }

    // Create
    @Test
    void givenNewCity_whenCreate_thenSuccessfullySaved() {
        City newCity = new City("thr", "third");

        when(
                repository.save(newCity.slug(), newCity)
        ).thenReturn(
                null
        );

        service.create(newCity.slug(), newCity.name());
        verify(repository, times(1)).save(newCity.slug(), newCity);
    }

    @Test
    void givenExistingCity_whenCreate_thenUpdate() {
        City modifiedCity = new City("fir", "second");

        service.create(modifiedCity.slug(), modifiedCity.name());
        verify(repository, times(1)).save(modifiedCity.slug(), modifiedCity);
    }

    // Update
    @Test
    void givenModifiedCity_whenUpdate_thenSuccessfullyUpdate() {
        City existingCity = new City("fir", "first");
        City modifiedCity = new City("fir", "second");

        when(
                repository.update(modifiedCity.slug(), modifiedCity)
        ).thenReturn(
                existingCity
        );

        service.update(modifiedCity.slug(), modifiedCity.name());
        verify(repository, times(1)).update(modifiedCity.slug(), modifiedCity);
    }

    @Test
    void givenNewCity_whenUpdate_thenThrowResourceNotFoundException() {
        City newCity = new City("thr", "third");

        when(
                repository.update(newCity.slug(), newCity)
        ).thenReturn(
                null
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.update(newCity.slug(), newCity.name())
        );
        verify(repository, times(1)).update(newCity.slug(), newCity);
    }

    // Delete
    @Test
    void givenExistingCity_whenDelete_thenSuccessfullyDeleted() {
        City existingCity = new City("fir", "first");

        when(
                repository.delete(existingCity.slug())
        ).thenReturn(
                existingCity
        );

        assertDoesNotThrow(() -> service.delete(existingCity.slug()));
        verify(repository, times(1)).delete(existingCity.slug());
    }

    @Test
    void givenNewCity_whenDelete_thenThrowResourceNotFoundException() {
        City newCity = new City("thr", "third");

        when(
                repository.delete(newCity.slug())
        ).thenReturn(
                null
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.delete(newCity.slug())
        );
        verify(repository, times(1)).delete(newCity.slug());
    }
}
