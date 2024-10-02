package ru.tbank.springapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.tbank.springapp.dto.CityDTO;
import ru.tbank.springapp.exception.ResourceNotFoundException;
import ru.tbank.springapp.service.CityService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CityController.class)
class CityControllerTest {

    private static final String BASE_URL = "/api/v1/locations";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CityService service;

    @Autowired
    private ObjectMapper mapper;

    // GetCities (success)
    @Test
    void given_whenGetCities_thenReturnOkWithJsonArray() throws Exception {
        CityDTO cityDTO = new CityDTO("fir", "first");
        String expectedResponseString = mapper.writeValueAsString(List.of(cityDTO));

        when(service.findAll()).thenReturn(List.of(cityDTO.toCity()));

        mockMvc
                .perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponseString));
    }

    // GetCity (success)
    @Test
    void givenExistingCityId_whenGetCity_thenReturnOkWithCity() throws Exception {
        CityDTO cityDTO = new CityDTO("fir", "first");
        String expectedResponseString = mapper.writeValueAsString(cityDTO);

        when(service.findById(cityDTO.slug())).thenReturn(cityDTO.toCity());

        mockMvc
                .perform(get(BASE_URL + "/" + cityDTO.slug()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponseString));
    }

    // GetCity (fail)
    @Test
    void givenNonExistingCityId_whenGetCity_thenReturnNotFound() throws Exception {
        CityDTO cityDTO = new CityDTO("fir", "first");

        when(service.findById(cityDTO.slug())).thenThrow(ResourceNotFoundException.class);

        mockMvc
                .perform(get(BASE_URL + "/" + cityDTO.slug()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // CreateCity (success)
    @Test
    void givenCityDTO_whenCreateCity_thenServiceCreatesCity() throws Exception {
        CityDTO cityDTO = new CityDTO("fir", "first");
        String requestBody = mapper.writeValueAsString(cityDTO);

        mockMvc
                .perform(
                        post(BASE_URL).content(requestBody).contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());

        verify(service, times(1)).create(cityDTO.slug(), cityDTO.name());
    }

    // UpdateCity (success)
    @Test
    void givenIdAndCityDTO_whenUpdateCity_thenServiceUpdatesCity() throws Exception {
        CityDTO cityDTO = new CityDTO("fir", "first");
        String id = cityDTO.slug();
        String requestBody = mapper.writeValueAsString(cityDTO);

        mockMvc
                .perform(
                        put(BASE_URL + "/" + id)
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());

        verify(service, times(1)).update(cityDTO.slug(), cityDTO.name());
    }

    // UpdateCity (fail)
    @Test
    void givenNonExistentId_whenUpdateCity_thenReturnNotFound() throws Exception {
        CityDTO cityDTO = new CityDTO("fir", "first");
        String id = cityDTO.slug();
        String requestBody = mapper.writeValueAsString(cityDTO);

        when(service.update(cityDTO.slug(), cityDTO.name())).thenThrow(ResourceNotFoundException.class);

        mockMvc
                .perform(
                        put(BASE_URL + "/" + id)
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

    // DeleteCity (success)
    @Test
    void givenId_whenDeleteCity_thenServiceDeletesCity() throws Exception {
        String id = "fir";

        mockMvc
                .perform(delete(BASE_URL + "/" + id))
                .andDo(print())
                .andExpect(status().isOk());

        verify(service, times(1)).delete(id);
    }

    // DeleteCity (fail)
    @Test
    void givenNonExistentId_whenDeleteCity_thenReturnNotFound() throws Exception {
        String id = "fir";

        when(service.delete(id)).thenThrow(ResourceNotFoundException.class);

        mockMvc
                .perform(delete(BASE_URL + "/" + id))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).delete(id);
    }
}