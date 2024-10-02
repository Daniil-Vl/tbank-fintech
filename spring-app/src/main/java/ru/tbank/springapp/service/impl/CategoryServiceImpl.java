package ru.tbank.springapp.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tbank.springapp.dao.Repository;
import ru.tbank.springapp.exception.ResourceNotFoundException;
import ru.tbank.springapp.model.Category;
import ru.tbank.springapp.service.CategoryService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final Repository<String, Category> categoryRepository;

    @Override
    public List<Category> findAll() {
        log.info("Trying to get all categories");
        return categoryRepository.getAll();
    }

    @Override
    public Category findById(String slug) {
        log.info("Trying to find category by id {}", slug);
        Category category = categoryRepository.getById(slug);
        if (category == null) {
            throw new ResourceNotFoundException("Category with slug " + slug + " not found");
        }
        return category;
    }

    @Override
    public Category create(String slug, String name) {
        Category category = new Category(slug, name);
        log.info("Trying to create category {}", category);
        return categoryRepository.save(slug, category);
    }

    @Override
    public Category update(String slug, String name) {
        log.info("Trying to update category with id {}", slug);

        Category category = new Category(slug, name);
        Category previousCategory = categoryRepository.update(slug, category);

        if (previousCategory == null)
            throw new ResourceNotFoundException("Category with slug " + slug + " not found");

        return previousCategory;
    }

    @Override
    public Category delete(String slug) {
        log.info("Trying to remove category with id {}", slug);

        Category deleted = categoryRepository.delete(slug);

        if (deleted == null)
            throw new ResourceNotFoundException("Category with slug " + slug + " not found");

        return deleted;
    }
}
