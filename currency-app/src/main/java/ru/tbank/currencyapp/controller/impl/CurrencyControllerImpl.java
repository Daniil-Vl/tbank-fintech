package ru.tbank.currencyapp.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.currencyapp.controller.CurrencyController;
import ru.tbank.currencyapp.dto.CurrencyConvertRequestDTO;
import ru.tbank.currencyapp.dto.CurrencyConvertedDTO;
import ru.tbank.currencyapp.dto.CurrencyRateDTO;
import ru.tbank.currencyapp.service.currency.CurrencyService;

@RestController
@RequestMapping("/currencies")
@RequiredArgsConstructor
public class CurrencyControllerImpl implements CurrencyController {

    private final CurrencyService currencyService;

    @Override
    public CurrencyRateDTO getCurrencyRate(String code) {
        return currencyService.getRate(code);
    }

    @Override
    public CurrencyConvertedDTO convertCurrency(CurrencyConvertRequestDTO request) {
        return currencyService.convertCurrencies(
                request.fromCurrency(), request.toCurrency(), request.amount()
        );
    }
}
