package ru.tbank.springapp.service.utils.patterns.observer;

import java.util.List;

public interface Subscriber {
    void update(List<?> data);
}
