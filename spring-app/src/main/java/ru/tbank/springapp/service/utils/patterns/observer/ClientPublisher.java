package ru.tbank.springapp.service.utils.patterns.observer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.tbank.springapp.client.impl.KudagoClientImpl;
import ru.tbank.springapp.dto.CategoryDTO;
import ru.tbank.springapp.dto.CityDTO;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ClientPublisher extends KudagoClientImpl implements Publisher {

    private final List<Subscriber> subscribers = new ArrayList<>();

    public ClientPublisher(RestClient restClient) {
        super(restClient);
    }

    @Override
    public List<CategoryDTO> getCategories() {
        List<CategoryDTO> categories = super.getCategories();
        notifySubscribers(categories);
        return categories;
    }

    @Override
    public List<CityDTO> getCities() {
        List<CityDTO> cities = super.getCities();
        notifySubscribers(cities);
        return cities;
    }

    @Override
    public void subscribe(Subscriber subscriber) {
        subscribers.add(subscriber);
        log.info("New subscriber: {}", subscriber);
    }

    @Override
    public void notifySubscribers(List<?> data) {
        for (Subscriber subscriber : subscribers) {
            subscriber.update(data);
        }
    }

}
