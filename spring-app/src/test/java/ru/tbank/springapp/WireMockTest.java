package ru.tbank.springapp;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.wiremock.integrations.testcontainers.WireMockContainer;

@Testcontainers
@ActiveProfiles("test")
public abstract class WireMockTest {

    @Container
    protected static final WireMockContainer wireMockContainer = new WireMockContainer(
            DockerImageName.parse("wiremock/wiremock:2.35.0")
    );

    static {
        wireMockContainer.start();
        WireMock.configureFor(wireMockContainer.getHost(), wireMockContainer.getFirstMappedPort());
    }

}
