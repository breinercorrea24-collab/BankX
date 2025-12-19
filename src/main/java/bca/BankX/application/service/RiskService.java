package bca.bankX.application.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import bca.bankX.domain.model.RiskRule;
import bca.bankX.infrastructure.adapter.out.RiskRuleRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class RiskService {
    private final RiskRuleRepository riskRepo;

    public Mono<Boolean> isAllowed(String currency, String type, BigDecimal amount) {
        return Mono.fromCallable(() -> riskRepo.findFirstByCurrency(currency)
                .map(RiskRule::getMaxDebitPerTx)
                .orElse(new BigDecimal("0")))
                .subscribeOn(Schedulers.boundedElastic()) // bloqueante a elastic
                .map(max -> {
                    System.out.println("Risk check for " + type + " " + amount + " " + max);
                    if ("DEBIT".equalsIgnoreCase(type)) {
                        return amount.compareTo(max) <= 0;
                    }
                    return true;
                });
    }
}
