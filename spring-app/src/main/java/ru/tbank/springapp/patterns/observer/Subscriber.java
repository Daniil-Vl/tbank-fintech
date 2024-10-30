package ru.tbank.springapp.patterns.observer;

import java.util.List;

public interface Subscriber {
    void update(List<?> data);
}
