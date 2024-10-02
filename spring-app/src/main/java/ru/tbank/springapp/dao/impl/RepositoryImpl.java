package ru.tbank.springapp.dao.impl;

import lombok.RequiredArgsConstructor;
import ru.tbank.springapp.dao.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

@RequiredArgsConstructor
public class RepositoryImpl<K, V> implements Repository<K, V> {

    private final ConcurrentMap<K, V> map;

    @Override
    public List<V> getAll() {
        return new ArrayList<>(map.values());
    }

    @Override
    public V getById(K slug) {
        return map.get(slug);
    }

    @Override
    public V save(K slug, V t) {
        return map.put(slug, t);
    }

    @Override
    public V update(K slug, V t) {
        V val = map.get(slug);
        if (val != null)
            return save(slug, t);
        return null;
    }

    @Override
    public V delete(K slug) {
        return map.remove(slug);
    }

}
