package ru.tbank.springapp.patterns.snapshot.category;

import ru.tbank.springapp.model.Category;

import java.util.List;

public class CategoryRepositoryMemento {

    private final List<Category> state;

    public CategoryRepositoryMemento(List<Category> categories) {
        this.state = categories;
    }

    public List<Category> getState() {
        return state;
    }
}
