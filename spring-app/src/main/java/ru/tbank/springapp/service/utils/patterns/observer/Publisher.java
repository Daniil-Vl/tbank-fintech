package ru.tbank.springapp.service.utils.patterns.observer;

import java.util.List;

public interface Publisher {
    void subscribe(Subscriber subscriber);
    void notifySubscribers(List<?> data);
}
