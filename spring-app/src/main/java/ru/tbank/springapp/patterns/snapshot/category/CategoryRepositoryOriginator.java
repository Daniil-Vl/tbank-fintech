package ru.tbank.springapp.patterns.snapshot.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.tbank.springapp.dao.Repository;
import ru.tbank.springapp.model.Category;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class CategoryRepositoryOriginator implements Repository<String, Category> {

    private final Repository<String, Category> repository;

    @Override
    public List<Category> getAll() {
        return repository.getAll();
    }

    @Override
    public Category getById(String slug) {
        return repository.getById(slug);
    }

    @Override
    public Category save(String slug, Category t) {
        return repository.save(slug, t);
    }

    @Override
    public Category update(String slug, Category t) {
        return repository.update(slug, t);
    }

    @Override
    public Category delete(String slug) {
        return repository.delete(slug);
    }

    public CategoryRepositoryMemento save() {
        return new CategoryRepositoryMemento(getAll());
    }

    public void restore(CategoryRepositoryMemento memento) {
        log.info("Restoring category repository");
        for (var category : getAll()) {
            delete(category.slug());
        }

        List<Category> state = memento.getState();
        for (var category : state) {
            save(category.slug(), category);
        }
    }
}
