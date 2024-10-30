package ru.tbank.springapp.patterns.snapshot.city;

import ru.tbank.springapp.model.City;

import java.util.List;

public class CityRepositoryMemento {
    private final List<City> state;

    public CityRepositoryMemento(List<City> cities) {
        this.state = cities;
    }

    public List<City> getState() {
        return state;
    }
}
