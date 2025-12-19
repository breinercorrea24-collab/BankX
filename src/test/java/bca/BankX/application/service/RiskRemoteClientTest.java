package bca.bankX.application.service;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static org.junit.Assert.assertEquals;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class RiskRemoteClientTest {

    @Test
    void testUriBuilderIsExecuted() throws Exception {

        try (MockWebServer server = new MockWebServer()) {

            server.enqueue(new MockResponse()
                    .setBody("true")
                    .addHeader("Content-Type", "application/json"));

            server.start();

            WebClient webClient = WebClient.builder()
                    .baseUrl(server.url("/").toString())
                    .build();

            RiskRemoteClient client = new RiskRemoteClient(webClient);

            var result = client.isAllowed(
                    "PEN",
                    "DEBIT",
                    new BigDecimal("100"));

            StepVerifier.create(result)
                    .expectNext(true)
                    .verifyComplete();

            var recordedRequest = server.takeRequest();
            assertEquals(
                    "/allow?currency=PEN&type=DEBIT&amount=100&fail=false&delayMs=200",
                    recordedRequest.getPath());
        }
    }
}