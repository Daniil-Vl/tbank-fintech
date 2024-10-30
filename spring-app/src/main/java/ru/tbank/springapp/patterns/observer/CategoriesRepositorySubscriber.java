package ru.tbank.springapp.patterns.observer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tbank.springapp.dao.Repository;
import ru.tbank.springapp.dto.CategoryDTO;
import ru.tbank.springapp.model.Category;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoriesRepositorySubscriber implements Subscriber {

    private final Repository<String, Category> categoryRepository;

    @Override
    public void update(List<?> data) {
        log.info("Category repository notified");

        if (data.isEmpty())
            return;

        Object first = data.getFirst();
        if (first instanceof CategoryDTO) {
            log.info("Updating categories");
            data.forEach(obj -> {
                CategoryDTO category = (CategoryDTO) obj;
                categoryRepository.save(category.slug(), category.toCategory());
            });
        }
    }

}
