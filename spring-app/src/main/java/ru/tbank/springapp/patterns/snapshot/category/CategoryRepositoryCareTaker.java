package ru.tbank.springapp.patterns.snapshot.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.tbank.springapp.dao.Repository;
import ru.tbank.springapp.model.Category;

import java.util.Deque;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class CategoryRepositoryCareTaker implements Repository<String, Category> {
    private final CategoryRepositoryOriginator originator;
    private final Deque<CategoryRepositoryMemento> history;


    @Override
    public List<Category> getAll() {
        return originator.getAll();
    }

    @Override
    public Category getById(String slug) {
        return originator.getById(slug);
    }

    @Override
    public Category save(String slug, Category t) {
        history.addLast(originator.save());
        return originator.save(slug, t);
    }

    @Override
    public Category update(String slug, Category t) {
        history.addLast(originator.save());
        return originator.update(slug, t);
    }

    @Override
    public Category delete(String slug) {
        history.addLast(originator.save());
        return originator.delete(slug);
    }

    public void undo() {
        log.info("undo");
        originator.restore(history.pollLast());
    }
}
