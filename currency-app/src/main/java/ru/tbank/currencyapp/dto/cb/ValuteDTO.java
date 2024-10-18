package ru.tbank.currencyapp.dto.cb;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * DTO for parsing response's application/xml content after requesting currency info from central bank api
 */
@ToString
public class ValuteDTO {

    @XmlAttribute(name = "ID")
    public String id;

    @XmlElement(name = "NumCode")
    public Integer numCode;

    @XmlElement(name = "CharCode")
    public String charCode;

    @XmlElement(name = "Nominal")
    public Integer nominal;

    @XmlElement(name = "Name")
    public String name;

    @XmlElement(name = "Value")
    @XmlJavaTypeAdapter(value = DoubleAdapter.class)
    public BigDecimal value;

    @XmlElement(name = "VunitRate")
    @XmlJavaTypeAdapter(value = DoubleAdapter.class)
    public BigDecimal rate;

    private static class DoubleAdapter extends XmlAdapter<String, BigDecimal> {
        @Override
        public BigDecimal unmarshal(String v) {
            return new BigDecimal(v.replace(",", "."));
        }

        @Override
        public String marshal(BigDecimal v) {
            return v.toString().replace('.', ',');
        }
    }

}


