package ru.tbank.springapp.client.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.tbank.springapp.client.KudagoClient;
import ru.tbank.springapp.dto.CategoryDTO;
import ru.tbank.springapp.dto.CityDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KudagoClientImpl implements KudagoClient {

    private final RestClient restClient;

    @Override
    public List<CategoryDTO> getCategories() {
        log.info("Try to get all categories from Kudago API");
        return restClient
                .get()
                .uri("/place-categories/")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    @Override
    public List<CityDTO> getCities() {
        log.info("Try to get all cities from Kudago API");
        return restClient
                .get()
                .uri("/locations/")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }


}
