package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Slf4j
public class App {

    private final static Path CITY_RESOURCES_PATH = Path.of("app/src/main/resources/city");

    public static void main(String[] args) throws IOException {
        Optional<City> city = readCity(CITY_RESOURCES_PATH.resolve("city.json"));
        if (city.isPresent()) {
            saveCityXml(city.get(), CITY_RESOURCES_PATH.resolve("city.xml"));
        }

        Optional<City> cityWithError = readCity(CITY_RESOURCES_PATH.resolve("city-error.json"));
        if (cityWithError.isPresent()) {
            saveCityXml(cityWithError.get(), CITY_RESOURCES_PATH.resolve("city-error.xml"));
        }
    }

    public static Optional<City> readCity(Path filename) {
        ObjectMapper mapper = new ObjectMapper();
        log.debug("Trying to read city from {}", filename);

        try {
            City city = mapper.readValue(filename.toFile(), City.class);
            log.info("Read city from {}", filename);
            return Optional.of(city);
        } catch (IOException e) {
            log.error("Failed to read and parse {}", filename);
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    public static void saveCityXml(City city, Path filename) throws IOException {
        log.info("Saving city {} to {}", city, filename);
        Files.writeString(filename, city.toXML());
    }

}
