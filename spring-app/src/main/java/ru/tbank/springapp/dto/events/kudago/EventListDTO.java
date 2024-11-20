package ru.tbank.springapp.dto.events.kudago;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record EventListDTO(
        @JsonProperty(value = "count") Integer count,
        @JsonProperty(value = "next") String next,
        @JsonProperty(value = "previous") String previous,
        @JsonProperty(value = "results") List<EventKudaGODTO> events
) {
}
