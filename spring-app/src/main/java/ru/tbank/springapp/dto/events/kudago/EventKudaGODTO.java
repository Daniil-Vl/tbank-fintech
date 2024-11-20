package ru.tbank.springapp.dto.events.kudago;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ru.tbank.springapp.dto.events.EventResponseDTO;
import ru.tbank.springapp.utils.serialization.EventDTODateDeserializer;

import java.time.LocalDate;
import java.util.List;

public record EventKudaGODTO(
        Integer id,
        List<Date> dates,
        String title,
        String description,
        String price,
        @JsonProperty(value = "is_free") Boolean isFree,
        @JsonProperty(value = "site_url") String siteUrl
) {
    public EventResponseDTO toEventDTO() {
        return new EventResponseDTO(title, description, price, siteUrl);
    }

    @JsonDeserialize(using = EventDTODateDeserializer.class)
    public record Date(
            LocalDate start,
            LocalDate end
    ) {
    }
}
