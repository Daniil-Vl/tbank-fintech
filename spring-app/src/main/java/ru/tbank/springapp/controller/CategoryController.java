package ru.tbank.springapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.springapp.aspect.Timed;
import ru.tbank.springapp.dto.CategoryDTO;
import ru.tbank.springapp.model.Category;
import ru.tbank.springapp.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/places/categories")
@RequiredArgsConstructor
@Timed
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    List<CategoryDTO> getCategories() {
        return categoryService.findAll().stream().map(Category::toDTO).toList();
    }

    @GetMapping("/{id}")
    CategoryDTO getCategory(@PathVariable String id) {
        return categoryService.findById(id).toDTO();
    }

    @PostMapping
    void createCategory(@RequestBody CategoryDTO categoryDTO) {
        categoryService.create(categoryDTO.slug(), categoryDTO.name());
    }

    @PutMapping("/{id}")
    void updateCategory(@PathVariable String id, @RequestBody CategoryDTO categoryDTO) {
        categoryService.update(id, categoryDTO.name());
    }

    @DeleteMapping("/{id}")
    void deleteCategory(@PathVariable String id) {
        categoryService.delete(id);
    }

}
