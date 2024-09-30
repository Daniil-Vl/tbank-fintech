package ru.tbank.springapp.service;

import ru.tbank.springapp.model.City;

import java.util.List;

public interface CityService {
    List<City> findAll();

    City findById(String slug);

    City create(String slug, String name);

    City update(String slug, String name);

    City delete(String slug);
}
