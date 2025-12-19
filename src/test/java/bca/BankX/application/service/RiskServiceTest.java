package bca.bankx.application.service;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import bca.bankx.application.service.RiskService;
import reactor.test.StepVerifier;

@SpringBootTest
class RiskServiceTest {
    @Autowired
    RiskService riskService;

    @Test
    void allowsDebitUnderLimit() {
        StepVerifier.create(riskService.isAllowed("PEN", "DEBIT", new BigDecimal("100")))
                .expectNext(true).verifyComplete();
    }

    @Test
    void deniesDebitOverLimit() {
        StepVerifier.create(riskService.isAllowed("PEN", "DEBIT", new BigDecimal("2000")))
                .expectNext(false).verifyComplete();
    }

    @Test
    void allowsCreditAlways() {
        StepVerifier.create(riskService.isAllowed("PEN", "CREDIT", new BigDecimal("10000")))
                .expectNext(true).verifyComplete();
    }

    @Test
    void deniesDebitForUnknownCurrency() {
        StepVerifier.create(riskService.isAllowed("EUR", "DEBIT", new BigDecimal("1")))
                .expectNext(false).verifyComplete();
    }

    @Test
    void allowsOtherTypeForUnknownCurrency() {
        StepVerifier.create(riskService.isAllowed("EUR", "CREDIT", new BigDecimal("10000")))
                .expectNext(true).verifyComplete();
    }

    @Test
    void allowsDebitAtLimit() {
        StepVerifier.create(riskService.isAllowed("PEN", "DEBIT", new BigDecimal("1500")))
                .expectNext(true).verifyComplete();
    }
}