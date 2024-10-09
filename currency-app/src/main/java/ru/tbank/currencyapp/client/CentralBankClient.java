package ru.tbank.currencyapp.client;

import ru.tbank.currencyapp.dto.cb.CBCurrencyResponseDTO;

import java.util.List;

public interface CentralBankClient {
    /**
     * Gather info about all currencies from central bank api
     *
     * @return list of currency DTOs
     */
    List<CBCurrencyResponseDTO> getCurrencies();
}
