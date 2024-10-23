package ru.tbank.springapp.service.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.tbank.springapp.aspect.Timed;
import ru.tbank.springapp.client.KudagoClient;
import ru.tbank.springapp.dao.Repository;
import ru.tbank.springapp.dto.CategoryDTO;
import ru.tbank.springapp.dto.PlaceDTO;
import ru.tbank.springapp.model.Category;
import ru.tbank.springapp.model.City;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
class DataFetcher {

    private final KudagoClient client;
    private final Repository<String, Category> categoryRepository;
    private final Repository<String, City> cityRepository;

    @Timed
    @EventListener(ApplicationReadyEvent.class)
    void fetchData() {
        log.info("Fetching data from Kudago API...");

        List<CategoryDTO> categories = client.getCategories();
        log.info("Fetched categories: {}", categories);

        List<PlaceDTO> cities = client.getCities();
        log.info("Fetched cities: {}", cities);

        log.info("Saving fetched data...");

        categories.forEach(categoryDTO -> {
            categoryRepository.save(categoryDTO.slug(), categoryDTO.toCategory());
        });

        cities.forEach(cityDTO -> {
            cityRepository.save(cityDTO.slug(), cityDTO.toCity());
        });

        log.info("Fetched data successfully saved");
    }

}
