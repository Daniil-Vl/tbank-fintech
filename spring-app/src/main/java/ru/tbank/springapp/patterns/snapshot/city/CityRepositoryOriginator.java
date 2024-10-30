package ru.tbank.springapp.patterns.snapshot.city;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.tbank.springapp.dao.Repository;
import ru.tbank.springapp.model.City;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class CityRepositoryOriginator implements Repository<String, City> {

    private final Repository<String, City> repository;

    @Override
    public List<City> getAll() {
        return repository.getAll();
    }

    @Override
    public City getById(String slug) {
        return repository.getById(slug);
    }

    @Override
    public City save(String slug, City t) {
        return repository.save(slug, t);
    }

    @Override
    public City update(String slug, City t) {
        return repository.update(slug, t);
    }

    @Override
    public City delete(String slug) {
        return repository.delete(slug);
    }

    public CityRepositoryMemento save() {
        return new CityRepositoryMemento(getAll());
    }

    public void restore(CityRepositoryMemento memento) {
        log.info("Restoring city repository");

        for (var category : getAll()) {
            delete(category.slug());
        }

        List<City> state = memento.getState();
        for (var city : state) {
            save(city.slug(), city);
        }
    }
}
