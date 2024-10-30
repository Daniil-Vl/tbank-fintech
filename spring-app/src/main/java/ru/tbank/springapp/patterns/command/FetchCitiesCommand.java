package ru.tbank.springapp.patterns.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.tbank.springapp.client.KudagoClient;
import ru.tbank.springapp.dao.Repository;
import ru.tbank.springapp.model.City;

@RequiredArgsConstructor
@Slf4j
public class FetchCitiesCommand implements Command {

    private final KudagoClient client;
    private final Repository<String, City> repository;

    @Override
    public void execute() {
        log.info("Fetching locations from kudago");
        client.getCities().forEach(cityDTO -> {
            repository.save(cityDTO.slug(), cityDTO.toCity());
        });
    }
}
