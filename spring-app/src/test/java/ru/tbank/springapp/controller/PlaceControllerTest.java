package ru.tbank.springapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.tbank.springapp.dto.PlaceDTO;
import ru.tbank.springapp.exception.ResourceNotFoundException;
import ru.tbank.springapp.service.PlaceService;
import ru.tbank.springapp.service.jwt.JwtService;
import ru.tbank.springapp.service.user.UserService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = PlaceController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class PlaceControllerTest {

    private static final String BASE_URL = "/api/v1/places";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlaceService service;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    // GetPlaces (success)
    @Test
    void given_whenGetPlaces_thenReturnOkWithJsonArray() throws Exception {
        PlaceDTO placeDTO = new PlaceDTO("fir", "first");
        String expectedResponseString = mapper.writeValueAsString(List.of(placeDTO));

        when(service.findAll()).thenReturn(List.of(placeDTO));

        mockMvc
                .perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponseString));
    }

    // GetPlace (success)
    @Test
    void givenExistingPlaceId_whenGetPlace_thenReturnOkWithPlace() throws Exception {
        PlaceDTO placeDTO = new PlaceDTO("fir", "first");
        String expectedResponseString = mapper.writeValueAsString(placeDTO);

        when(service.findById(placeDTO.slug())).thenReturn(placeDTO);

        mockMvc
                .perform(get(BASE_URL + "/" + placeDTO.slug()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponseString));
    }

    // GetPlace (fail)
    @Test
    void givenNonExistingPlaceId_whenGetPlace_thenReturnNotFound() throws Exception {
        PlaceDTO placeDTO = new PlaceDTO("fir", "first");

        when(service.findById(placeDTO.slug())).thenThrow(ResourceNotFoundException.class);

        mockMvc
                .perform(get(BASE_URL + "/" + placeDTO.slug()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // CreatePlace (success)
    @Test
    void givenPlaceDTO_whenCreatePlace_thenServiceCreatesPlace() throws Exception {
        PlaceDTO placeDTO = new PlaceDTO("fir", "first");
        String requestBody = mapper.writeValueAsString(placeDTO);

        mockMvc
                .perform(
                        post(BASE_URL).content(requestBody).contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());

        verify(service, times(1)).create(placeDTO.slug(), placeDTO.name());
    }

    // UpdatePlace (success)
    @Test
    void givenIdAndPlaceDTO_whenUpdatePlace_thenServiceUpdatesPlace() throws Exception {
        PlaceDTO placeDTO = new PlaceDTO("fir", "first");
        String id = placeDTO.slug();
        String requestBody = mapper.writeValueAsString(placeDTO);

        mockMvc
                .perform(
                        put(BASE_URL + "/" + id)
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());

        verify(service, times(1)).update(placeDTO.slug(), placeDTO.slug(), placeDTO.name());
    }

    // UpdatePlace (fail)
    @Test
    void givenNonExistentId_whenUpdatePlace_thenReturnNotFound() throws Exception {
        PlaceDTO placeDTO = new PlaceDTO("fir", "first");
        String id = placeDTO.slug();
        String requestBody = mapper.writeValueAsString(placeDTO);

        when(service.update(placeDTO.slug(), placeDTO.slug(), placeDTO.name())).thenThrow(ResourceNotFoundException.class);

        mockMvc
                .perform(
                        put(BASE_URL + "/" + id)
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

    // DeletePlace (success)
    @Test
    void givenId_whenDeletePlace_thenServiceDeletesPlace() throws Exception {
        String id = "fir";

        mockMvc
                .perform(delete(BASE_URL + "/" + id))
                .andDo(print())
                .andExpect(status().isOk());

        verify(service, times(1)).delete(id);
    }

    // DeletePlace (fail)
    @Test
    void givenNonExistentId_whenDeletePlace_thenReturnNotFound() throws Exception {
        String id = "fir";

        when(service.delete(id)).thenThrow(ResourceNotFoundException.class);

        mockMvc
                .perform(delete(BASE_URL + "/" + id))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).delete(id);
    }
}