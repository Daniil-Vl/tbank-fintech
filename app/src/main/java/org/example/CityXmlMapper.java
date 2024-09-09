package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CityXmlMapper {

    private static final XmlMapper xmlMapper = new XmlMapper();

    public static String toXML(City city) throws JsonProcessingException {
        log.debug("Trying to format city {} to xml", city);
        return xmlMapper.writeValueAsString(city);
    }

}
