package bca.bankx.infraestructura.in.rest;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import bca.bankx.application.service.RiskRemoteClient;
import bca.bankx.infrastructure.adapter.in.rest.request.CreateTxRequest;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TransactionControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RiskRemoteClient riskRemoteClient;

    @BeforeEach
    void setUp() {
        Mockito.when(riskRemoteClient.isAllowed(Mockito.anyString(), Mockito.anyString(), Mockito.any(BigDecimal.class)))
            .thenReturn(Mono.just(true));
    }

    @Test
    void testCreateTransaction_Success() {
        CreateTxRequest req = new CreateTxRequest();
        req.setAccountNumber("001-0001");
        req.setType("DEBIT");
        req.setAmount(new BigDecimal("100"));

        webTestClient.post()
            .uri("/api/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(req)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.status").isEqualTo("OK")
            .jsonPath("$.amount").isEqualTo(100);
    }

    @Test
    void testCreateTransaction_AccountNotFound() {
        CreateTxRequest req = new CreateTxRequest();
        req.setAccountNumber("999-9999");
        req.setType("DEBIT");
        req.setAmount(new BigDecimal("10"));

        webTestClient.post()
            .uri("/api/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(req)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.error").isEqualTo("account_not_found");
    }

    @Test
    void testCreateTransaction_InsufficientFunds() {
        CreateTxRequest req = new CreateTxRequest();
        req.setAccountNumber("001-0001");
        req.setType("DEBIT");
        req.setAmount(new BigDecimal("3000"));

        webTestClient.post()
            .uri("/api/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(req)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.error").isEqualTo("insufficient_funds");
    }

    @Test
    void testCreateTransaction_RiskRejected() {
        Mockito.when(riskRemoteClient.isAllowed(Mockito.anyString(), Mockito.eq("DEBIT"), Mockito.eq(new BigDecimal("2000"))))
            .thenReturn(Mono.just(false));

        CreateTxRequest req = new CreateTxRequest();
        req.setAccountNumber("001-0001");
        req.setType("DEBIT");
        req.setAmount(new BigDecimal("2000"));

        webTestClient.post()
            .uri("/api/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(req)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.error").isEqualTo("risk_rejected");
    }

    @Test
    void testListTransactions() {
        // First create a transaction
        CreateTxRequest req = new CreateTxRequest();
        req.setAccountNumber("001-0001");
        req.setType("DEBIT");
        req.setAmount(new BigDecimal("50"));

        webTestClient.post()
            .uri("/api/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(req)
            .exchange()
            .expectStatus().isCreated();

        // Then list
        webTestClient.get()
            .uri("/api/transactions?accountNumber=001-0001")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Object.class)
            .hasSize(1);
    }

    @Test
    void testStreamTransactions() {
        webTestClient.get()
            .uri("/api/stream/transactions")
            .accept(MediaType.TEXT_EVENT_STREAM)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM);
    }
}
