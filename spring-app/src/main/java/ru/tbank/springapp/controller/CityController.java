package ru.tbank.springapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.tbank.springapp.aspect.Timed;
import ru.tbank.springapp.dto.CityDTO;
import ru.tbank.springapp.model.City;
import ru.tbank.springapp.service.CityService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
@Timed
public class CityController {

    private final CityService cityService;

    @GetMapping
    List<CityDTO> getCities() {
        return cityService.findAll().stream().map(City::toDTO).toList();
    }

    @GetMapping("/{id}")
    CityDTO getCity(@PathVariable String id) {
        return cityService.findById(id).toDTO();
    }

    @PostMapping
    void createCity(@RequestBody CityDTO cityDTO) {
        cityService.create(cityDTO.slug(), cityDTO.name());
    }

    @PutMapping("/{id}")
    void updateCity(@PathVariable String id, @RequestBody CityDTO cityDTO) {
        cityService.update(id, cityDTO.name());
    }

    @DeleteMapping("/{id}")
    void deleteCity(@PathVariable String id) {
        cityService.delete(id);
    }

}
