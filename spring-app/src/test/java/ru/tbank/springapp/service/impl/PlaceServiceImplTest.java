package ru.tbank.springapp.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.springapp.dao.jpa.PlaceRepository;
import ru.tbank.springapp.dto.PlaceDTO;
import ru.tbank.springapp.exception.ResourceNotFoundException;
import ru.tbank.springapp.model.entities.PlaceEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceServiceImplTest {

    @Mock
    private PlaceRepository placeRepository;

    @InjectMocks
    private PlaceServiceImpl service;

    // FindAll
    @Test
    void givenOnePlace_whenFindAll_thenRepositoryGetAllCall() {
        service.findAll();
        verify(placeRepository, times(1)).findAll();
    }

    // FindById
    @Test
    void givenOnePlace_whenFindById_thenReturnPlace() {
        String name = "first";
        String slug = "fir";

        PlaceEntity existingPlace = new PlaceEntity();
        existingPlace.setName(name);
        existingPlace.setSlug(slug);

        when(
                placeRepository.findBySlug(slug)
        ).thenReturn(
                Optional.of(existingPlace)
        );

        PlaceDTO placeDTO = service.findById(slug);

        verify(placeRepository, times(1)).findBySlug(slug);
        assertEquals(PlaceDTO.fromPlaceEntity(existingPlace), placeDTO);
    }

    @Test
    void givenNewPlace_whenFindById_thenThrowResourceNotFoundException() {
        String name = "first";
        String slug = "fir";

        PlaceEntity newPlaceEntity = new PlaceEntity();
        newPlaceEntity.setName(name);
        newPlaceEntity.setSlug(slug);

        when(
                placeRepository.findBySlug(slug)
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.findById(newPlaceEntity.getSlug())
        );
        verify(placeRepository, times(1)).findBySlug(slug);
    }

    // Create
    @Test
    void givenNewPlace_whenCreate_thenSuccessfullySaved() {
        String name = "first";
        String slug = "fir";

        PlaceEntity newPlaceEntity = new PlaceEntity();
        newPlaceEntity.setName(name);
        newPlaceEntity.setSlug(slug);

        PlaceDTO placeDTO = service.create(slug, name);

        verify(placeRepository, times(1)).save(any(PlaceEntity.class));
        assertEquals(PlaceDTO.fromPlaceEntity(newPlaceEntity), placeDTO);
    }

    @Test
    void givenExistingPlace_whenCreate_thenUpdate() {
        String name = "first";
        String slug = "fir";

        PlaceEntity modifiedPlaceEntity = new PlaceEntity();
        modifiedPlaceEntity.setName(name);
        modifiedPlaceEntity.setSlug(slug);

        PlaceDTO placeDTO = service.create(slug, name);

        verify(placeRepository, times(1)).save(any(PlaceEntity.class));
        assertEquals(PlaceDTO.fromPlaceEntity(modifiedPlaceEntity), placeDTO);
    }

    // Update
    @Test
    void givenModifiedPlace_whenUpdate_thenSuccessfullyUpdate() {
        String slug = "fir";
        String newName = "second";

        when(
                placeRepository.updateBySlug(slug, newName)
        ).thenReturn(
                1
        );

        service.update(slug, newName);

        verify(placeRepository, times(1)).updateBySlug(slug, newName);
    }

    @Test
    void givenNewPlace_whenUpdate_thenThrowResourceNotFoundException() {
        String slug = "fir";
        String newName = "second";

        when(
                placeRepository.updateBySlug(slug, newName)
        ).thenReturn(
                0
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.update(slug, newName)
        );
        verify(placeRepository, times(1)).updateBySlug(slug, newName);
    }

    // Delete
    @Test
    void givenExistingPlace_whenDelete_thenSuccessfullyDeleted() {
        String name = "first";
        String slug = "fir";

        when(
                placeRepository.deleteBySlug(slug)
        ).thenReturn(
                1
        );

        assertDoesNotThrow(() -> service.delete(slug));
        verify(placeRepository, times(1)).deleteBySlug(slug);
    }

    @Test
    void givenNewPlace_whenDelete_thenThrowResourceNotFoundException() {
        String slug = "fir";

        when(
                placeRepository.deleteBySlug(slug)
        ).thenReturn(
                0
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.delete(slug)
        );
        verify(placeRepository, times(1)).deleteBySlug(slug);
    }
}