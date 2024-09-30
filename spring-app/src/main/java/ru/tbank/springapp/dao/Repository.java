package ru.tbank.springapp.dao;

import java.util.List;

public interface Repository<K, V> {
    List<V> getAll();

    V getById(K slug);

    V save(K slug, V t);

    V update(K slug, V t);

    V delete(K slug);
}
