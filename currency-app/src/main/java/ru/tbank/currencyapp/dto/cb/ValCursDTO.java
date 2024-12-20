package ru.tbank.currencyapp.dto.cb;

import jakarta.xml.bind.annotation.*;
import lombok.ToString;

import java.util.List;

/**
 * DTO for parsing response's application/xml content after requesting currency info from central bank api
 */
@XmlRootElement(name = "ValCurs")
@XmlAccessorType(XmlAccessType.FIELD)
@ToString
public class ValCursDTO {

    @XmlAttribute(name = "Date")
    public String date;

    @XmlAttribute(name = "name")
    public String name;

    @XmlElement(name = "Valute")
    public List<ValuteDTO> valuteDTOS;

}

