package bca.bankX.infraestructura.in.rest.mock;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MockRiskControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testAllow_DebitLowAmount_ShouldReturnTrue() {
        webTestClient.get()
            .uri(uri -> uri.path("/mock/risk/allow")
                .queryParam("currency", "PEN")
                .queryParam("type", "DEBIT")
                .queryParam("amount", "1000")
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody(Boolean.class).isEqualTo(true);
    }

    @Test
    void testAllow_DebitHighAmount_ShouldReturnFalse() {
        webTestClient.get()
            .uri(uri -> uri.path("/mock/risk/allow")
                .queryParam("currency", "PEN")
                .queryParam("type", "DEBIT")
                .queryParam("amount", "1500")
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody(Boolean.class).isEqualTo(false);
    }

    @Test
    void testAllow_Credit_ShouldReturnTrue() {
        webTestClient.get()
            .uri(uri -> uri.path("/mock/risk/allow")
                .queryParam("currency", "USD")
                .queryParam("type", "CREDIT")
                .queryParam("amount", "2000")
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody(Boolean.class).isEqualTo(true);
    }

    @Test
    void testAllow_FailTrue_ShouldReturnError() {
        webTestClient.get()
            .uri(uri -> uri.path("/mock/risk/allow")
                .queryParam("currency", "PEN")
                .queryParam("type", "DEBIT")
                .queryParam("amount", "1000")
                .queryParam("fail", "true")
                .build())
            .exchange()
            .expectStatus().is5xxServerError();
    }

    @Test
    void testAllow_WithDelay_ShouldReturnTrue() {
        webTestClient.get()
            .uri(uri -> uri.path("/mock/risk/allow")
                .queryParam("currency", "PEN")
                .queryParam("type", "DEBIT")
                .queryParam("amount", "1000")
                .queryParam("delayMs", "100")
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody(Boolean.class).isEqualTo(true);
    }
}
