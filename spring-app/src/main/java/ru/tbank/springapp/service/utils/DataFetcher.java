package ru.tbank.springapp.service.utils;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tbank.springapp.aspect.Timed;
import ru.tbank.springapp.client.KudagoClient;
import ru.tbank.springapp.dao.Repository;
import ru.tbank.springapp.dao.jpa.PlaceRepository;
import ru.tbank.springapp.dto.CategoryDTO;
import ru.tbank.springapp.dto.PlaceDTO;
import ru.tbank.springapp.model.Category;
import ru.tbank.springapp.model.entities.PlaceEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
@Slf4j
class DataFetcher {

    private final KudagoClient client;
    private final Repository<String, Category> categoryRepository;
    private final PlaceRepository placeRepository;

    private final ExecutorService fetchingExecutorService;

    @Timed
    @SneakyThrows
    void fetchData() {
        log.info("Fetching data from Kudago API...");

        Future<?> categoriesFuture = fetchingExecutorService.submit(this::fetchCategories);
        Future<?> citiesFuture = fetchingExecutorService.submit(this::fetchCities);

        categoriesFuture.get();
        citiesFuture.get();

        log.info("Fetched data successfully saved");
    }

    private void fetchCategories() {
        var categories = client.getCategories();

        log.info("Fetched categories: {}", categories);
        log.info("Saving fetched categories...");

        categories.forEach(categoryDTO -> {
            categoryRepository.save(categoryDTO.slug(), categoryDTO.toCategory());
        });

        log.info("Fetched categories successfully saved");
    }

    private void fetchCities() {
        var places = client.getCities();

        log.info("Fetched cities: {}", cities);
        log.info("Saving fetched cities...");
      
        places.forEach(cityDTO -> {
            placeRepository.save(
                    PlaceEntity.builder()
                            .slug(cityDTO.slug())
                            .name(cityDTO.name())
                            .build()
            );

        log.info("Fetched cities successfully saved");
    }
}
