package bca.bankx.domain.exception;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import bca.bankx.application.service.RiskRemoteClient;
import bca.bankx.infrastructure.adapter.in.rest.request.CreateTxRequest;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GlobalErrorHandlerTest {

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
    void testHandleBusinessException_AccountNotFound() {
        CreateTxRequest req = new CreateTxRequest();
        req.setAccountNumber("999-9999"); // non-existent account
        req.setType("DEBIT");
        req.setAmount(new BigDecimal("10"));

        webTestClient.post()
            .uri("/api/transactions")
            .bodyValue(req)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.error").isEqualTo("account_not_found");
    }

    @Test
    void testHandleBusinessException_InsufficientFunds() {
        CreateTxRequest req = new CreateTxRequest();
        req.setAccountNumber("001-0001");
        req.setType("DEBIT");
        req.setAmount(new BigDecimal("3000")); // more than balance

        webTestClient.post()
            .uri("/api/transactions")
            .bodyValue(req)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.error").isEqualTo("insufficient_funds");
    }

    @Test
    void testHandleBusinessException_RiskRejected() {
        Mockito.when(riskRemoteClient.isAllowed(Mockito.anyString(), Mockito.eq("DEBIT"), Mockito.eq(new BigDecimal("2000"))))
            .thenReturn(Mono.just(false));

        CreateTxRequest req = new CreateTxRequest();
        req.setAccountNumber("001-0001");
        req.setType("DEBIT");
        req.setAmount(new BigDecimal("2000"));

        webTestClient.post()
            .uri("/api/transactions")
            .bodyValue(req)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.error").isEqualTo("risk_rejected");
    }

}
