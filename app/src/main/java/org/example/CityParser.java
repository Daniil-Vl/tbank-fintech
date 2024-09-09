package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@Slf4j
public class CityParser {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static Optional<City> readCityFromJson(Path filename) {
        log.debug("Trying to read city from {}", filename);

        try {
            City city = mapper.readValue(filename.toFile(), City.class);
            log.info("Read city from {}", filename);
            return Optional.of(city);
        } catch (IOException e) {
            log.error("Failed to read and parse {}", filename, e);
            return Optional.empty();
        }
    }

}
