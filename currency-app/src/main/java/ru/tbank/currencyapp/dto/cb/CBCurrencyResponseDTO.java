package ru.tbank.currencyapp.dto.cb;

import java.math.BigDecimal;

public record CBCurrencyResponseDTO(
        Integer numCode,
        String charCode,
        Integer nominal,
        String name,
        BigDecimal value,
        BigDecimal vUnitRate
) {
    public static CBCurrencyResponseDTO fromValute(ValuteDTO valuteDTO) {
        return new CBCurrencyResponseDTO(
                valuteDTO.numCode,
                valuteDTO.charCode,
                valuteDTO.nominal,
                valuteDTO.name,
                valuteDTO.value,
                valuteDTO.rate
        );
    }
}
