package ru.tbank.springapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import ru.tbank.springapp.configuration.ApplicationConfig;

@SpringBootApplication
@EnableConfigurationProperties(value = {ApplicationConfig.class})
@EnableAsync
public class SpringAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAppApplication.class, args);
    }

}
