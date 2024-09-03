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

    public String toXML() {
        log.debug("Trying to convert city {} to xml", this);

        String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<city>\n" +
                "<slug>" + slug + "</slug>\n" +
                "<coords>\n" +
                "\t<lat>" + coords.getLat() + "</lat>\n" +
                "\t<lon>" + coords.getLon() + "</lon>\n" +
                "</coords>\n" +
                "</city>\n";

        return xmlString;
    }

    @Getter
    @Setter
    @ToString
    private static class Coords {
        private double lat;
        private double lon;
    }

}