package ru.tbank.springapp.patterns.observer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tbank.springapp.dao.Repository;
import ru.tbank.springapp.dto.CityDTO;
import ru.tbank.springapp.model.City;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CitiesRepositorySubscriber implements Subscriber {

    private final Repository<String, City> cityRepository;

    @Override
    public void update(List<?> data) {
        log.info("City repository notified");

        if (data.isEmpty())
            return;

        Object first = data.getFirst();
        if (first instanceof CityDTO) {
            log.info("Updating cities");
            data.forEach(obj -> {
                CityDTO city = (CityDTO) obj;
                cityRepository.save(city.slug(), city.toCity());
            });
        }
    }

}
