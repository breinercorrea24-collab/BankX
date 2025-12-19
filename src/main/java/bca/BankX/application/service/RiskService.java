package bca.bankx.application.service;

import bca.bankx.domain.model.RiskRule;
import bca.bankx.infrastructure.adapter.out.RiskRuleRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Legacy risk service.
 */
@Service
@RequiredArgsConstructor
public class RiskService {
  private final RiskRuleRepository riskRepo;

  /**
   * Checks if the transaction is allowed.
   *
   * @param currency the currency
   * @param type the transaction type
   * @param amount the amount
   * @return Mono of Boolean
   */
  public Mono<Boolean> isAllowed(String currency, String type,
      BigDecimal amount) {
    return Mono.fromCallable(() -> riskRepo.findFirstByCurrency(currency)
        .map(RiskRule::getMaxDebitPerTx)
        .orElse(new BigDecimal("0")))
        .subscribeOn(Schedulers.boundedElastic()) // bloqueante a elastic
        .map(max -> {
          System.out.println("Risk check for " + type + " " + amount + " "
              + max);
          if ("DEBIT".equalsIgnoreCase(type)) {
            return amount.compareTo(max) <= 0;
          }
          return true;
        });
  }
}
