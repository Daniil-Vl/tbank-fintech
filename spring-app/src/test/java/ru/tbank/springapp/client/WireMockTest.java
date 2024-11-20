package ru.tbank.springapp.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.wiremock.integrations.testcontainers.WireMockContainer;

@Testcontainers
public abstract class WireMockTest {

    @Container
    protected static final WireMockContainer wireMockContainer = new WireMockContainer(
            DockerImageName.parse("wiremock/wiremock:2.35.0")
    );

    protected static void initServer() {
        wireMockContainer.start();
        WireMock.configureFor(wireMockContainer.getHost(), wireMockContainer.getFirstMappedPort());
    }

}
