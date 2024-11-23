package ru.tbank.springapp.client;

import ru.tbank.springapp.dto.CategoryDTO;
import ru.tbank.springapp.dto.PlaceDTO;

import java.util.List;

public interface KudagoClient {
    List<CategoryDTO> getCategories();

    List<PlaceDTO> getCities();
}
