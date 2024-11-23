package ru.tbank.springapp.service;

import ru.tbank.springapp.dto.PlaceDTO;

import java.util.List;

public interface PlaceService {
    List<PlaceDTO> findAll();

    PlaceDTO findById(String slug);

    PlaceDTO create(String slug, String name);

    int update(String slug, String newSlug, String name);

    int delete(String slug);
}
