package ru.tbank.springapp.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tbank.springapp.dao.Repository;
import ru.tbank.springapp.dao.impl.RepositoryImpl;
import ru.tbank.springapp.model.Category;
import ru.tbank.springapp.model.City;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RepositoryConfiguration {

    @Bean
    public Repository<String, Category> categoryRepository() {
        return new RepositoryImpl<>(new ConcurrentHashMap<>());
    }

    @Bean
    public Repository<String, City> cityRepository() {
        return new RepositoryImpl<>(new ConcurrentHashMap<>());
    }

}
