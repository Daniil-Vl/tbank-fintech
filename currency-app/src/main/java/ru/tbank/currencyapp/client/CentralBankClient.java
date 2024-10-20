package ru.tbank.currencyapp.client;

import ru.tbank.currencyapp.dto.cb.CBCurrencyResponseDTO;

import java.util.Map;

public interface CentralBankClient {
    /**
     * Gather info about all currencies from central bank api
     *
     * @return map of currency DTOs
     */
    Map<String, CBCurrencyResponseDTO> getCurrencies();
}
