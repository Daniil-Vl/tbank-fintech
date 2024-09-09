package org.example;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.example.CityParser.readCityFromJson;
import static org.example.CityXmlMapper.toXML;

@Slf4j
public class App {

    private final static Path CITY_RESOURCES_PATH = Path.of("app/src/main/resources/city");

    public static void main(String[] args) throws IOException {
        Optional<City> city = readCityFromJson(CITY_RESOURCES_PATH.resolve("city.json"));
        if (city.isPresent()) {
            saveCity(city.get(), CITY_RESOURCES_PATH.resolve("city.xml"));
        }

        Optional<City> cityWithError = readCityFromJson(CITY_RESOURCES_PATH.resolve("city-error.json"));
        if (cityWithError.isPresent()) {
            saveCity(cityWithError.get(), CITY_RESOURCES_PATH.resolve("city-error.xml"));
        }
    }

    public static void saveCity(City city, Path filename) throws IOException {
        log.info("Saving city {} to {}", city, filename);
        String cityXML = toXML(city);
        Files.writeString(filename, cityXML);
    }

}
