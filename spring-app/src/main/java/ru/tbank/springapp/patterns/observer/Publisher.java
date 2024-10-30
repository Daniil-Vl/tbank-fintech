package ru.tbank.springapp.patterns.observer;

import java.util.List;

public interface Publisher {
    void subscribe(Subscriber subscriber);
    void notifySubscribers(List<?> data);
}
