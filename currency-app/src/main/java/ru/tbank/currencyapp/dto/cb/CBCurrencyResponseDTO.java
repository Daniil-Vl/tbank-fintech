package ru.tbank.currencyapp.dto.cb;

public record CBCurrencyResponseDTO(
        Integer numCode,
        String charCode,
        Integer nominal,
        String name,
        Double value,
        Double vUnitRate
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
