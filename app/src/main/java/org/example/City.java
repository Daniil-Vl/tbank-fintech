package org.example;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Slf4j
public class City {

    private String slug;
    private Coords coords;

    @Getter
    @Setter
    @ToString
    private static class Coords {
        private double lat;
        private double lon;
    }

}