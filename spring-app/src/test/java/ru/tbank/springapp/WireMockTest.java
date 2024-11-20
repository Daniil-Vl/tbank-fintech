package ru.tbank.springapp;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.extension.RegisterExtension;


public abstract class WireMockTest {

    @RegisterExtension
    protected static WireMockExtension wireMockExtension = WireMockExtension
            .newInstance()
            .build();

}
