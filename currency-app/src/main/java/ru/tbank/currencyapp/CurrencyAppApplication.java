package ru.tbank.currencyapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.tbank.currencyapp.config.ApplicationConfig;

@SpringBootApplication
@EnableConfigurationProperties(ApplicationConfig.class)
@EnableCaching
@EnableScheduling
public class CurrencyAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(CurrencyAppApplication.class, args);
    }

}
