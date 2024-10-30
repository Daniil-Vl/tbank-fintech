package ru.tbank.springapp.patterns.snapshot.city;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.tbank.springapp.dao.Repository;
import ru.tbank.springapp.model.City;

import java.util.Deque;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class CityRepositoryCareTaker implements Repository<String, City> {

    private final CityRepositoryOriginator originator;
    private final Deque<CityRepositoryMemento> history;

    @Override
    public List<City> getAll() {
        return originator.getAll();
    }

    @Override
    public City getById(String slug) {
        return originator.getById(slug);
    }

    @Override
    public City save(String slug, City t) {
        history.addLast(originator.save());
        return originator.save(slug, t);
    }

    @Override
    public City update(String slug, City t) {
        history.addLast(originator.save());
        return originator.update(slug, t);
    }

    @Override
    public City delete(String slug) {
        history.addLast(originator.save());
        return originator.delete(slug);
    }

    public void undo() {
        log.info("undo");
        originator.restore(history.pollLast());
    }
}
