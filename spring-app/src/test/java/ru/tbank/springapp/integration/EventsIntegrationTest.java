package ru.tbank.springapp.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.tbank.springapp.dao.jpa.EventRepository;
import ru.tbank.springapp.dao.jpa.PlaceRepository;
import ru.tbank.springapp.dto.EventDTO;
import ru.tbank.springapp.dto.EventRequestDTO;
import ru.tbank.springapp.model.entities.EventEntity;
import ru.tbank.springapp.model.entities.PlaceEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EventsIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @AfterEach
    void cleanDatabase() {
        eventRepository.deleteAll();
        placeRepository.deleteAll();
    }

    // Get all events
    @Test
    void givenEvents_whenRequestGetEvents_thenRetrieveAllEvents() throws Exception {
        PlaceEntity placeEntity = new PlaceEntity();
        placeEntity.setName("Saint-Petersburg");
        placeEntity.setSlug("spb");

        EventEntity event1 = new EventEntity();
        event1.setStartDate(LocalDate.now());
        event1.setName("first event");
        event1.setSlug("first");
        event1.setPlace(placeEntity);

        EventEntity event2 = new EventEntity();
        event2.setStartDate(LocalDate.now());
        event2.setName("second event");
        event2.setSlug("second");
        event2.setPlace(placeEntity);

        placeRepository.save(placeEntity);
        event1 = eventRepository.save(event1);
        event2 = eventRepository.save(event2);

        List<EventDTO> expectedListOfEvents = List.of(
                EventDTO.fromEvent(event1),
                EventDTO.fromEvent(event2)
        );
        String expectedResponseBody = objectMapper.writeValueAsString(expectedListOfEvents);

        mockMvc
                .perform(get("/api/v1/events"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(expectedResponseBody)
                );
    }

    // Get event by id
    @Test
    void givenEventId_whenRequestGetEventById_thenReturnEvent() throws Exception {
        PlaceEntity placeEntity = new PlaceEntity();
        placeEntity.setName("Saint-Petersburg");
        placeEntity.setSlug("spb");

        EventEntity event = new EventEntity();
        event.setStartDate(LocalDate.now());
        event.setName("first event");
        event.setSlug("first");
        event.setPlace(placeEntity);

        placeRepository.save(placeEntity);
        event = eventRepository.save(event);

        EventDTO expectedEvent = EventDTO.fromEvent(event);
        String expectedResponseBody = objectMapper.writeValueAsString(expectedEvent);

        mockMvc
                .perform(get("/api/v1/events/" + event.getId()))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(expectedResponseBody)
                );
    }

    @Test
    void givenNonExistentEventId_whenRequestGetEventById_thenReturnNotFound() throws Exception {
        mockMvc
                .perform(get("/api/v1/events/1"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // Create event
    @Test
    void givenEventRequestDTO_whenRequestCreateEvent_thenSuccessfullyCreatesEvent() throws Exception {
        PlaceEntity placeEntity = new PlaceEntity();
        placeEntity.setName("Saint-Petersburg");
        placeEntity.setSlug("spb");
        placeRepository.save(placeEntity);

        EventRequestDTO eventRequestDTO = new EventRequestDTO(
                LocalDate.now(),
                "new event",
                "event",
                "spb"
        );
        String requestBody = objectMapper.writeValueAsString(eventRequestDTO);

        mockMvc
                .perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk());

        EventEntity event = eventRepository.findAll().getFirst();
        assertEquals(eventRequestDTO.date(), event.getStartDate());
        assertEquals(eventRequestDTO.name(), event.getName());
        assertEquals(eventRequestDTO.slug(), event.getSlug());
        assertEquals(eventRequestDTO.placeSlug(), event.getPlace().getSlug());
    }

    @Test
    void givenEventDTOWithNonExistentPlace_whenRequestCreateEvent_thenReturnNotFound() throws Exception {
        EventRequestDTO eventRequestDTO = new EventRequestDTO(
                LocalDate.now(),
                "new event",
                "event",
                "spb"
        );
        String requestBody = objectMapper.writeValueAsString(eventRequestDTO);

        mockMvc
                .perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // Update event
    @Test
    void givenEventRequestDTO_whenRequestUpdateEvent_thenSuccessfullyUpdatesEvent() throws Exception {
        PlaceEntity placeEntity = new PlaceEntity();
        placeEntity.setName("Saint-Petersburg");
        placeEntity.setSlug("spb");
        placeRepository.save(placeEntity);

        EventEntity event = new EventEntity();
        event.setId(1L);
        event.setStartDate(LocalDate.now());
        event.setName("first event");
        event.setSlug("first");
        event.setPlace(placeEntity);
        event = eventRepository.save(event);

        EventRequestDTO eventRequestDTO = new EventRequestDTO(
                LocalDate.now(),
                "updated event",
                "event",
                "spb"
        );
        String requestBody = objectMapper.writeValueAsString(eventRequestDTO);

        mockMvc
                .perform(put("/api/v1/events/" + event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk());

        EventEntity modifiedEvent = eventRepository.findAll().getFirst();
        assertEquals(eventRequestDTO.date(), modifiedEvent.getStartDate());
        assertEquals(eventRequestDTO.name(), modifiedEvent.getName());
        assertEquals(eventRequestDTO.slug(), modifiedEvent.getSlug());
        assertEquals(eventRequestDTO.placeSlug(), modifiedEvent.getPlace().getSlug());
    }

    @Test
    void givenNonExistentPlaceSlug_whenRequestUpdateEvent_thenReturnNotFound() throws Exception {
        PlaceEntity placeEntity = new PlaceEntity();
        placeEntity.setName("Saint-Petersburg");
        placeEntity.setSlug("spb");
        placeRepository.save(placeEntity);

        EventEntity event = new EventEntity();
        event.setId(1L);
        event.setStartDate(LocalDate.now());
        event.setName("first event");
        event.setSlug("first");
        event.setPlace(placeEntity);
        event = eventRepository.save(event);

        EventRequestDTO eventRequestDTO = new EventRequestDTO(
                LocalDate.now(),
                "updated event",
                "event",
                "msk"
        );
        String requestBody = objectMapper.writeValueAsString(eventRequestDTO);

        mockMvc
                .perform(put("/api/v1/events/" + event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void givenNonExistentEvent_whenRequestUpdateEvent_thenReturnNotFound() throws Exception {
        PlaceEntity placeEntity = new PlaceEntity();
        placeEntity.setName("Saint-Petersburg");
        placeEntity.setSlug("spb");
        placeRepository.save(placeEntity);

        EventEntity event = new EventEntity();
        event.setId(1L);
        event.setStartDate(LocalDate.now());
        event.setName("first event");
        event.setSlug("first");
        event.setPlace(placeEntity);
        event = eventRepository.save(event);

        EventRequestDTO eventRequestDTO = new EventRequestDTO(
                LocalDate.now(),
                "updated event",
                "event",
                "msk"
        );
        String requestBody = objectMapper.writeValueAsString(eventRequestDTO);

        mockMvc
                .perform(put("/api/v1/events/" + (event.getId() + 1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // Delete
    @Test
    void givenEventId_whenRequestDeleteEvent_thenSuccessfullyDeleteEvent() throws Exception {
        PlaceEntity placeEntity = new PlaceEntity();
        placeEntity.setName("Saint-Petersburg");
        placeEntity.setSlug("spb");
        placeRepository.save(placeEntity);

        EventEntity event = new EventEntity();
        event.setId(1L);
        event.setStartDate(LocalDate.now());
        event.setName("first event");
        event.setSlug("first");
        event.setPlace(placeEntity);
        event = eventRepository.save(event);

        mockMvc
                .perform(delete("/api/v1/events/" + event.getId()))
                .andDo(print())
                .andExpect(status().isOk());

        List<EventEntity> allEvents = eventRepository.findAll();
        assertFalse(allEvents.contains(event));
    }

    @Test
    void givenNonExistentEventId_whenRequestDeleteEvent_thenReturnNotFound() throws Exception {
        mockMvc
                .perform(delete("/api/v1/events/1"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

}
