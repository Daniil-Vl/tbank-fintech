package ru.tbank.springapp.utils.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import ru.tbank.springapp.dto.events.kudago.EventKudaGODTO;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class EventDTODateDeserializer extends StdDeserializer<EventKudaGODTO.Date> {

    protected EventDTODateDeserializer(Class<?> vc) {
        super(vc);
    }

    public EventDTODateDeserializer() {
        this(null);
    }

    @Override
    public EventKudaGODTO.Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        long start = node.get("start").numberValue().longValue();
        long end = node.get("end").numberValue().longValue();

        LocalDate startDate = Instant.ofEpochSecond(start).atOffset(ZoneOffset.UTC).toLocalDate();
        LocalDate endDate = Instant.ofEpochSecond(end).atOffset(ZoneOffset.UTC).toLocalDate();

        return new EventKudaGODTO.Date(startDate, endDate);
    }
}
