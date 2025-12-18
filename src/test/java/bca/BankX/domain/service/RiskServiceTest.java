package bca.BankX.domain.service;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import bca.BankX.application.service.RiskService;
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
}