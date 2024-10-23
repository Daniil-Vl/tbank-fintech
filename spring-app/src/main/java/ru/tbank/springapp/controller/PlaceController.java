package ru.tbank.springapp.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.tbank.springapp.aspect.Timed;
import ru.tbank.springapp.dto.PlaceDTO;
import ru.tbank.springapp.service.PlaceService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
@Timed
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping
    List<PlaceDTO> getPlaces() {
        return placeService.findAll();
    }

    @GetMapping("/{id}")
    PlaceDTO getPlace(
            @PathVariable @NotBlank String id
    ) {
        return placeService.findById(id);
    }

    @PostMapping
    void createPlace(
            @RequestBody @Valid PlaceDTO placeDTO
    ) {
        placeService.create(placeDTO.slug(), placeDTO.name());
    }

    @PutMapping("/{id}")
    void updatePlace(
            @PathVariable @NotBlank String id,
            @RequestBody @Valid PlaceDTO placeDTO
    ) {
        placeService.update(id, placeDTO.name());
    }

    @DeleteMapping("/{id}")
    void deletePlace(
            @PathVariable @NotBlank String id
    ) {
        placeService.delete(id);
    }

}
