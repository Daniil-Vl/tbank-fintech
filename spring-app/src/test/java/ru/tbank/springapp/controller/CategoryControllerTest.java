package ru.tbank.springapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.tbank.springapp.dto.CategoryDTO;
import ru.tbank.springapp.exception.ResourceNotFoundException;
import ru.tbank.springapp.service.CategoryService;
import ru.tbank.springapp.service.jwt.JwtService;
import ru.tbank.springapp.service.user.UserService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(value = CategoryController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class CategoryControllerTest {

    private static final String BASE_URL = "/api/v1/places/categories";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService service;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    // GetCategories (success)
    @Test
    void given_whenGetCategories_thenReturnOkWithJsonArray() throws Exception {
        CategoryDTO categoryDTO = new CategoryDTO("fir", "first");
        String expectedResponseString = mapper.writeValueAsString(List.of(categoryDTO));

        when(service.findAll()).thenReturn(List.of(categoryDTO.toCategory()));

        mockMvc
                .perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponseString));
    }

    // GetCategory (success)
    @Test
    void givenExistingCategoryId_whenGetCategory_thenReturnOkWithCategory() throws Exception {
        CategoryDTO categoryDTO = new CategoryDTO("fir", "first");
        String expectedResponseString = mapper.writeValueAsString(categoryDTO);

        when(service.findById(categoryDTO.slug())).thenReturn(categoryDTO.toCategory());

        mockMvc
                .perform(get(BASE_URL + "/" + categoryDTO.slug()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponseString));
    }

    // GetCategory (fail)
    @Test
    void givenNonExistingCategoryId_whenGetCategory_thenReturnNotFound() throws Exception {
        CategoryDTO categoryDTO = new CategoryDTO("fir", "first");

        when(service.findById(categoryDTO.slug())).thenThrow(ResourceNotFoundException.class);

        mockMvc
                .perform(get(BASE_URL + "/" + categoryDTO.slug()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // CreateCategory (success)
    @Test
    void givenCategoryDTO_whenCreateCategory_thenServiceCreatesCategory() throws Exception {
        CategoryDTO categoryDTO = new CategoryDTO("fir", "first");
        String requestBody = mapper.writeValueAsString(categoryDTO);

        mockMvc
                .perform(
                        post(BASE_URL).content(requestBody).contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());

        verify(service, times(1)).create(categoryDTO.slug(), categoryDTO.name());
    }

    // UpdateCategory (success)
    @Test
    void givenIdAndCategoryDTO_whenUpdateCategory_thenServiceUpdatesCategory() throws Exception {
        CategoryDTO categoryDTO = new CategoryDTO("fir", "first");
        String id = categoryDTO.slug();
        String requestBody = mapper.writeValueAsString(categoryDTO);

        mockMvc
                .perform(
                        put(BASE_URL + "/" + id)
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());

        verify(service, times(1)).update(categoryDTO.slug(), categoryDTO.name());
    }

    // UpdateCategory (fail)
    @Test
    void givenNonExistentId_whenUpdateCategory_thenReturnNotFound() throws Exception {
        CategoryDTO categoryDTO = new CategoryDTO("fir", "first");
        String id = categoryDTO.slug();
        String requestBody = mapper.writeValueAsString(categoryDTO);

        when(service.update(categoryDTO.slug(), categoryDTO.name())).thenThrow(ResourceNotFoundException.class);

        mockMvc
                .perform(
                        put(BASE_URL + "/" + id)
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

    // DeleteCategory (success)
    @Test
    void givenId_whenDeleteCategory_thenServiceDeletesCategory() throws Exception {
        String id = "fir";

        mockMvc
                .perform(delete(BASE_URL + "/" + id))
                .andDo(print())
                .andExpect(status().isOk());

        verify(service, times(1)).delete(id);
    }


    // DeleteCategory (fail)
    @Test
    void givenNonExistentId_whenDeleteCategory_thenReturnNotFound() throws Exception {
        String id = "fir";

        when(service.delete(id)).thenThrow(ResourceNotFoundException.class);

        mockMvc
                .perform(delete(BASE_URL + "/" + id))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(service, times(1)).delete(id);
    }
}