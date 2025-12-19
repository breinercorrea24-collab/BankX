package bca.bankX.application.service;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import bca.bankX.application.service.RiskRemoteClient;
import bca.bankX.application.service.TransactionService;
import bca.bankX.domain.exception.BusinessException;
import bca.bankX.infrastructure.adapter.in.rest.request.CreateTxRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class TransactionServiceTest {
    @Autowired
    TransactionService service;

    @MockitoBean
    RiskRemoteClient riskRemoteClient;

    @BeforeEach
    void setUp() {
        Mockito.when(riskRemoteClient.isAllowed(Mockito.anyString(), Mockito.anyString(), Mockito.any(BigDecimal.class)))
            .thenReturn(Mono.just(true));
    }

    @Test
    void debitOk() {
        var req = new CreateTxRequest();
        req.setAccountNumber("001-0001");
        req.setType("DEBIT");
        req.setAmount(new BigDecimal("100"));
        StepVerifier.create(service.create(req))
                .assertNext(tx -> assertEquals("OK", tx.getStatus()))
                .verifyComplete();
    }

    @Test
    void debitInsufficientFunds() {
        var req = new CreateTxRequest();
        req.setAccountNumber("001-0001");
        req.setType("DEBIT");
        req.setAmount(new BigDecimal("3000"));
        StepVerifier.create(service.create(req))
                .expectErrorMatches(e -> e instanceof BusinessException && "insufficient_funds".equals(e.getMessage()))
                .verify();
    }

    @Test
    void debitRiskRejected() {
        Mockito.when(riskRemoteClient.isAllowed(Mockito.anyString(), Mockito.eq("DEBIT"), Mockito.eq(new BigDecimal("2000"))))
            .thenReturn(Mono.just(false));
        var req = new CreateTxRequest();
        req.setAccountNumber("001-0001");
        req.setType("DEBIT");
        req.setAmount(new BigDecimal("2000"));
        StepVerifier.create(service.create(req))
                .expectErrorMatches(e -> e instanceof BusinessException && "risk_rejected".equals(e.getMessage()))
                .verify();
    }

    @Test
    void creditOk() {
        var req = new CreateTxRequest();
        req.setAccountNumber("001-0001");
        req.setType("CREDIT");
        req.setAmount(new BigDecimal("100"));
        StepVerifier.create(service.create(req))
                .assertNext(tx -> assertEquals("OK", tx.getStatus()))
                .verifyComplete();
    }

    @Test
    void accountNotFound() {
        var req = new CreateTxRequest();
        req.setAccountNumber("999-9999");
        req.setType("DEBIT");
        req.setAmount(new BigDecimal("10"));
        StepVerifier.create(service.create(req))
                .expectErrorMatches(e -> e instanceof BusinessException && "account_not_found".equals(e.getMessage()))
                .verify();
    }

    @Test
    void byAccountOk() {
        // Create a transaction first
        var req = new CreateTxRequest();
        req.setAccountNumber("001-0001");
        req.setType("DEBIT");
        req.setAmount(new BigDecimal("10"));
        service.create(req).block(); // create one transaction
        StepVerifier.create(service.byAccount("001-0001"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void byAccountNotFound() {
        StepVerifier.create(service.byAccount("999-9999"))
                .expectErrorMatches(e -> e instanceof BusinessException && "account_not_found".equals(e.getMessage()))
                .verify();
    }

    @Test
    void streamEmitsTransaction() {
        // Create a transaction and verify it's emitted in the stream
        var req = new CreateTxRequest();
        req.setAccountNumber("001-0002");
        req.setType("DEBIT");
        req.setAmount(new BigDecimal("50"));

        StepVerifier.create(service.stream().take(1)) // take first event
                .then(() -> service.create(req).block()) // trigger creation
                .assertNext(event -> assertEquals("transaction", event.event()))
                .verifyComplete();
    }
}
