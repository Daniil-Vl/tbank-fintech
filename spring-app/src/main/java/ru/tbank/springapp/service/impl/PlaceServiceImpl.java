package ru.tbank.springapp.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tbank.springapp.dao.jpa.PlaceRepository;
import ru.tbank.springapp.dto.PlaceDTO;
import ru.tbank.springapp.exception.ResourceNotFoundException;
import ru.tbank.springapp.model.entities.PlaceEntity;
import ru.tbank.springapp.service.PlaceService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlaceServiceImpl implements PlaceService {

    private final PlaceRepository placeRepository;

    @Override
    public List<PlaceDTO> findAll() {
        log.info("Trying to get all cities");
        return placeRepository
                .findAll()
                .stream()
                .map(PlaceDTO::fromPlaceEntity)
                .toList();
    }

    @Override
    public PlaceDTO findById(String slug) {
        log.info("Trying to find city by id {}", slug);

        Optional<PlaceEntity> placeEntity = placeRepository.findBySlug(slug);

        if (placeEntity.isEmpty())
            throw new ResourceNotFoundException("City with slug " + slug + " not found");

        return PlaceDTO.fromPlaceEntity(placeEntity.get());
    }

    @Override
    @Transactional
    public PlaceDTO create(String slug, String name) {
        log.info("Trying to create city with slug = {} and name = {}", slug, name);

        PlaceEntity placeEntity = new PlaceEntity();
        placeEntity.setSlug(slug);
        placeEntity.setName(name);

        placeRepository.save(placeEntity);
        log.info("Place with id {} created", placeEntity.getId());

        return PlaceDTO.fromPlaceEntity(placeEntity);
    }

    @Override
    @Transactional
    public int update(String slug, String name) {
        log.info("Trying to update city with id {}", slug);

        int rowsAffected = placeRepository.updateBySlug(slug, name);

        if (rowsAffected == 0)
            throw new ResourceNotFoundException("Place with slug " + slug + " not found");

        log.info("Place with slug {} updated", slug);

        return rowsAffected;
    }

    @Override
    @Transactional
    public int delete(String slug) {
        log.info("Trying to remove city with id {}", slug);

        int rowsAffected = placeRepository.deleteBySlug(slug);

        if (rowsAffected == 0)
            throw new ResourceNotFoundException("City with slug " + slug + " not found");

        log.info("Place with slug {} deleted", slug);

        return rowsAffected;
    }
}
