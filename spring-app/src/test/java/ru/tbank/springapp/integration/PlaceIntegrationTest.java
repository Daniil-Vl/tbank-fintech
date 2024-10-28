package ru.tbank.springapp.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.tbank.springapp.dao.jpa.PlaceRepository;
import ru.tbank.springapp.dto.PlaceDTO;
import ru.tbank.springapp.model.entities.PlaceEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PlaceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlaceRepository placeRepository;


    @AfterEach
    void cleanDatabase() {
        placeRepository.deleteAll();
    }

    // Get places
    @Test
    void givenPlaces_whenRequestGetPlaces_thenRetrieveAllPlaces() throws Exception {
        PlaceEntity placeEntity1 = PlaceEntity.builder()
                .name("Saint-Petersburg")
                .slug("spb")
                .build();

        PlaceEntity placeEntity2 = PlaceEntity.builder()
                .name("Moscow")
                .slug("msc")
                .build();

        placeEntity1 = placeRepository.save(placeEntity1);
        placeEntity2 = placeRepository.save(placeEntity2);

        List<PlaceDTO> places = List.of(
                PlaceDTO.fromPlaceEntity(placeEntity1),
                PlaceDTO.fromPlaceEntity(placeEntity2)
        );
        String expectedResponseBody = objectMapper.writeValueAsString(places);

        mockMvc
                .perform(get("/api/v1/places"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(expectedResponseBody)
                );
    }

    // Get Place by id
    @Test
    void givenPlaceSlug_whenRequestGetSlug_thenRetrievePlace() throws Exception {
        PlaceEntity placeEntity = PlaceEntity.builder()
                .name("Saint-Petersburg")
                .slug("spb")
                .build();
        placeEntity = placeRepository.save(placeEntity);

        PlaceDTO expectedPlace = PlaceDTO.fromPlaceEntity(placeEntity);
        String expectedResponseBody = objectMapper.writeValueAsString(expectedPlace);

        mockMvc
                .perform(get("/api/v1/places/" + placeEntity.getSlug()))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(expectedResponseBody)
                );
    }

    @Test
    void givenNonExistentPlaceSlug_whenRequestGetPlace_thenRetrievePlace() throws Exception {
        mockMvc
                .perform(get("/api/v1/places/10"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // Create place
    @Test
    void givenPlaceDTO_whenRequestCreatePlace_thenCreatePlace() throws Exception {
        // TODO
        PlaceDTO placeDTO = new PlaceDTO("spb", "Saint-Petersburg");
        String requestBody = objectMapper.writeValueAsString(placeDTO);

        mockMvc
                .perform(post("/api/v1/places")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk());

        PlaceEntity placeEntity = placeRepository.findAll().getFirst();
        assertEquals(placeEntity.getName(), placeDTO.name());
        assertEquals(placeEntity.getSlug(), placeDTO.slug());
    }

    // Update place
    @Test
    void givenPlaceDTOAndId_whenRequestUpdatePlace_thenUpdatePlace() throws Exception {
        PlaceEntity placeEntity = PlaceEntity.builder()
                .name("Saint-Petersburg")
                .slug("spb")
                .build();
        placeEntity = placeRepository.save(placeEntity);

        PlaceDTO placeDTO = new PlaceDTO("msc", "Moscow");
        String requestBody = objectMapper.writeValueAsString(placeDTO);

        mockMvc
                .perform(put("/api/v1/places/" + placeEntity.getSlug())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk());

        PlaceEntity modifiedPlace = placeRepository.findAll().getFirst();
        assertEquals(placeDTO.name(), modifiedPlace.getName());
        assertEquals(placeDTO.slug(), modifiedPlace.getSlug());
    }

    @Test
    void givenNonExistentPlace_whenRequestUpdatePlace_thenReturnNotFound() throws Exception {
        PlaceDTO placeDTO = new PlaceDTO("msc", "Moscow");
        String requestBody = objectMapper.writeValueAsString(placeDTO);

        mockMvc
                .perform(put("/api/v1/places/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // Delete place
    @Test
    void givenPlaceSlug_whenRequestDeletePlace_thenSuccessfullyDeletePlace() throws Exception {
        PlaceEntity placeEntity = PlaceEntity.builder()
                .name("Saint-Petersburg")
                .slug("spb")
                .build();
        placeEntity = placeRepository.save(placeEntity);

        mockMvc
                .perform(delete("/api/v1/places/" + placeEntity.getSlug()))
                .andDo(print())
                .andExpect(status().isOk());

        List<PlaceEntity> places = placeRepository.findAll();
        assertFalse(places.contains(placeEntity));
    }

    @Test
    void givenNonExistentPlace_whenRequestDeletePlace_thenReturnNotFound() throws Exception {
        mockMvc
                .perform(delete("/api/v1/places/1"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
