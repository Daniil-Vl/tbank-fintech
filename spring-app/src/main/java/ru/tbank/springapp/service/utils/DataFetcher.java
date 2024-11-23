package ru.tbank.springapp.service.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.tbank.springapp.aspect.Timed;
import ru.tbank.springapp.client.KudagoClient;
import ru.tbank.springapp.dao.Repository;
import ru.tbank.springapp.dao.jpa.PlaceRepository;
import ru.tbank.springapp.dto.CategoryDTO;
import ru.tbank.springapp.dto.PlaceDTO;
import ru.tbank.springapp.model.Category;
import ru.tbank.springapp.model.entities.PlaceEntity;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
class DataFetcher {

    private final KudagoClient client;
    private final Repository<String, Category> categoryRepository;
    private final PlaceRepository placeRepository;

    @Timed
    @EventListener(ApplicationReadyEvent.class)
    void fetchData() {
        log.info("Fetching data from Kudago API...");

        List<CategoryDTO> categories = client.getCategories();
        log.info("Fetched categories: {}", categories);

        List<PlaceDTO> places = client.getCities();
        log.info("Fetched places: {}", places);

        log.info("Saving fetched data...");

        categories.forEach(categoryDTO -> {
            categoryRepository.save(categoryDTO.slug(), categoryDTO.toCategory());
        });

        places.forEach(cityDTO -> {
            placeRepository.save(
                    PlaceEntity.builder()
                            .slug(cityDTO.slug())
                            .name(cityDTO.name())
                            .build()
            );
        });

        log.info("Fetched data successfully saved");
    }

}
