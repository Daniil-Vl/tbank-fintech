package ru.tbank.currencyapp.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.tbank.currencyapp.controller.CurrencyController;
import ru.tbank.currencyapp.dto.CurrencyConvertRequestDTO;
import ru.tbank.currencyapp.dto.CurrencyConvertedDTO;
import ru.tbank.currencyapp.dto.CurrencyRateDTO;
import ru.tbank.currencyapp.dto.error.ApiErrorDTO;
import ru.tbank.currencyapp.exception.exceptions.CurrencyNotFoundException;
import ru.tbank.currencyapp.exception.exceptions.ExternalSystemUnavailableException;
import ru.tbank.currencyapp.service.currency.CurrencyService;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CurrencyController.class)
@ExtendWith(MockitoExtension.class)
class CurrencyControllerImplTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyService currencyService;

    @Test
    void givenValidCode_whenGetCurrencyRate_thenGetRateFromService() throws Exception {
        String code = "AUD";

        CurrencyRateDTO expectedResponseBody = new CurrencyRateDTO(code, new BigDecimal("10.0"));
        String responseBodyJson = mapper.writeValueAsString(expectedResponseBody);

        when(currencyService.getRate(code)).thenReturn(expectedResponseBody);

        mockMvc
                .perform(get("/currencies/rates/{code}", code))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(responseBodyJson)
                );
    }

    @Test
    void givenInvalidCode_whenGetCurrencyRate_thenReturnApiError() throws Exception {
        String code = "Invalid_currency_code";

        ApiErrorDTO expectedResponseBody = new ApiErrorDTO(400, "[Currency is invalid]");
        String responseBodyJson = mapper.writeValueAsString(expectedResponseBody);

        mockMvc
                .perform(get("/currencies/rates/{code}", code))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().json(responseBodyJson)
                );
    }

    @Test
    void givenValidCurrenciesAndAmount_whenConvertCurrency_thenSuccessfullyConvertWithService() throws Exception {
        CurrencyConvertRequestDTO requestBody = new CurrencyConvertRequestDTO("AUD", "RUB", new BigDecimal("10.0"));
        String requestBodyJson = mapper.writeValueAsString(requestBody);

        CurrencyConvertedDTO expectedResponseBody = new CurrencyConvertedDTO("AUD", "RUB", new BigDecimal("100.0"));
        String responseBodyJson = mapper.writeValueAsString(expectedResponseBody);

        when(currencyService.convertCurrencies(requestBody.fromCurrency(), requestBody.toCurrency(), requestBody.amount()))
                .thenReturn(expectedResponseBody);

        mockMvc
                .perform(post("/currencies/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(responseBodyJson)
                );
    }

    @Test
    void givenInvalidCurrency_whenConvertCurrency_thenReturnApiErrorWithBadRequest() throws Exception {
        CurrencyConvertRequestDTO requestBody = new CurrencyConvertRequestDTO("ASDSADS", "RUB", new BigDecimal("10.0"));
        String requestBodyJson = mapper.writeValueAsString(requestBody);

        ApiErrorDTO apiErrorDTO = new ApiErrorDTO(400, "[, fromCurrency: Currency is invalid]");
        String responseBodyJson = mapper.writeValueAsString(apiErrorDTO);

        mockMvc
                .perform(post("/currencies/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().json(responseBodyJson)
                );
    }

    @Test
    void givenValidCurrencyNotInCbList_whenConvertCurrency_thenReturnApiErrorWithNotFound() throws Exception {
        CurrencyConvertRequestDTO requestBody = new CurrencyConvertRequestDTO("AUD", "RUB", new BigDecimal("10.0"));
        String requestBodyJson = mapper.writeValueAsString(requestBody);

        ApiErrorDTO apiErrorDTO = new ApiErrorDTO(404, "To currency not found: " + requestBody.toCurrency());
        String responseBodyJson = mapper.writeValueAsString(apiErrorDTO);

        when(currencyService.convertCurrencies(any(), eq(requestBody.toCurrency()), any()))
                .thenThrow(new CurrencyNotFoundException(String.format("To currency not found: %s", requestBody.toCurrency())));

        mockMvc
                .perform(post("/currencies/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().json(responseBodyJson)
                );
    }

    @Test
    void givenNegativeAmount_whenConvertCurrency_thenReturnApiErrorWithBadRequest() throws Exception {
        CurrencyConvertRequestDTO requestBody = new CurrencyConvertRequestDTO("AUD", "RUB", new BigDecimal("-10.0"));
        String requestBodyJson = mapper.writeValueAsString(requestBody);

        ApiErrorDTO apiErrorDTO = new ApiErrorDTO(400, "[, amount: amount must be non negative]");
        String responseBodyJson = mapper.writeValueAsString(apiErrorDTO);

        mockMvc
                .perform(post("/currencies/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().json(responseBodyJson)
                );
    }

    @Test
    void givenUnavailableCentralBankApi_whenConvertCurrency_thenReturnApiErrorWithServiceUnavailable() throws Exception {
        CurrencyConvertRequestDTO requestBody = new CurrencyConvertRequestDTO("AUD", "RUB", new BigDecimal("10.0"));
        String requestBodyJson = mapper.writeValueAsString(requestBody);

        ApiErrorDTO apiErrorDTO = new ApiErrorDTO(503, "Central bank api unavailable");
        String responseBodyJson = mapper.writeValueAsString(apiErrorDTO);

        when(currencyService.convertCurrencies(any(), any(), any()))
                .thenThrow(new ExternalSystemUnavailableException("Central bank api unavailable"));

        mockMvc
                .perform(post("/currencies/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andDo(print())
                .andExpectAll(
                        status().isServiceUnavailable(),
                        content().json(responseBodyJson)
                );
    }

}
