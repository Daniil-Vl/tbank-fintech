package ru.tbank.springapp.service;

import ru.tbank.springapp.dto.EventDTO;

import java.time.LocalDate;
import java.util.List;

public interface EventService {

    List<EventDTO> findAll();

    EventDTO findById(long id);

    EventDTO create(LocalDate date, String name, String slug, String placeName);

    int update(int id, LocalDate date, String name, String slug, String placeName);

    long delete(long id);

}
