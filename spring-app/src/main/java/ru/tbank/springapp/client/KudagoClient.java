package ru.tbank.springapp.client;

import ru.tbank.springapp.dto.CategoryDTO;
import ru.tbank.springapp.dto.CityDTO;

import java.util.List;

public interface KudagoClient {
    List<CategoryDTO> getCategories();

    List<CityDTO> getCities();
}
