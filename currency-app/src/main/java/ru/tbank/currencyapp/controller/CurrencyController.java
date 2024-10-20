package ru.tbank.currencyapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.tbank.currencyapp.dto.CurrencyConvertRequestDTO;
import ru.tbank.currencyapp.dto.CurrencyConvertedDTO;
import ru.tbank.currencyapp.dto.CurrencyRateDTO;
import ru.tbank.currencyapp.dto.error.ApiErrorDTO;
import ru.tbank.currencyapp.validation.annotations.CurrencyIsValid;

@Tag(name = "Currency Controller")
@RequestMapping("/currencies")
public interface CurrencyController {

    @Operation(summary = "Get the exchange rate relative to the ruble")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CurrencyRateDTO.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters in query or in body",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Currency not found in central bank currencies list",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(
                    responseCode = "503",
                    description = "Central bank currency service unavailable",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorDTO.class))
            )
    })
    @GetMapping("/rates/{code}")
    CurrencyRateDTO getCurrencyRate(
            @PathVariable @CurrencyIsValid String code
    );

    @Operation(summary = "Transfer an amount from one currency to another")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully converted",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CurrencyConvertedDTO.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters in query or in body",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Currency not found in central bank currencies list",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorDTO.class))),
            @ApiResponse(
                    responseCode = "503",
                    description = "Central bank currency service unavailable",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorDTO.class))
            )
    })
    @PostMapping("/convert")
    CurrencyConvertedDTO convertCurrency(
            @Validated @RequestBody CurrencyConvertRequestDTO request
    );

}
