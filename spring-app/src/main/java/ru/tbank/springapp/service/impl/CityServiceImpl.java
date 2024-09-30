package ru.tbank.springapp.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tbank.springapp.dao.Repository;
import ru.tbank.springapp.exception.ResourceNotFoundException;
import ru.tbank.springapp.model.City;
import ru.tbank.springapp.service.CityService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CityServiceImpl implements CityService {

    private final Repository<String, City> cityRepository;

    @Override
    public List<City> findAll() {
        log.info("Trying to get all cities");
        return cityRepository.getAll();
    }

    @Override
    public City findById(String slug) {
        log.info("Trying to find city by id {}", slug);
        City city = cityRepository.getById(slug);
        if (city == null) {
            throw new ResourceNotFoundException("City with slug " + slug + " not found");
        }
        return city;
    }

    @Override
    public City create(String slug, String name) {
        City city = new City(slug, name);
        log.info("Trying to create city {}", city);
        return cityRepository.save(slug, city);
    }

    @Override
    public City update(String slug, String name) {
        log.info("Trying to update city with id {}", slug);
        City city = new City(slug, name);
        return cityRepository.update(slug, city);
    }

    @Override
    public City delete(String slug) {
        log.info("Trying to remove city with id {}", slug);
        return cityRepository.delete(slug);
    }
}
