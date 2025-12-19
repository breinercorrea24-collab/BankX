package bca.bankx.application.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.math.BigDecimal;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Remote client for risk assessment service.
 */
@Service
@RequiredArgsConstructor
public class RiskRemoteClient {

  @Autowired
  RiskService legacy; // el de JPA que ya tenías

  private final WebClient riskWebClient;

  /**
   * Checks if the transaction is allowed based on risk criteria.
   *
   * @param currency the currency
   * @param type the transaction type
   * @param amount the amount
   * @return Mono of Boolean indicating if allowed
   */
  @Retry(name = "riskClient")
  @CircuitBreaker(name = "riskClient", fallbackMethod = "fallback")
  public Mono<Boolean> isAllowed(String currency, String type,
      BigDecimal amount) {
    System.out.println("RiskRemoteClient - Checking risk for currency="
        + currency + ", type=" + type + ", amount=" + amount);
    return riskWebClient.get()
        .uri(uri -> uri.path("/allow")
            .queryParam("currency", currency)
            .queryParam("type", type)
            .queryParam("amount", amount)
            .queryParam("fail", false)
            .queryParam("delayMs", 200) // simula latencia
            .build())
        .retrieve()
        .bodyToMono(Boolean.class)
        .timeout(Duration.ofSeconds(1)); // ⬅ timeout reactivo
  }

  /**
   * Fallback method for circuit breaker.
   *
   * @param currency the currency
   * @param type the transaction type
   * @param amount the amount
   * @param ex the exception
   * @return Mono of Boolean from legacy service
   */
  public Mono<Boolean> fallback(String currency, String type,
      BigDecimal amount, Throwable ex) {
    return legacyAllowed(currency, type, amount);
  }

  private Mono<Boolean> legacyAllowed(String c, String t, BigDecimal a) {
    return legacy.isAllowed(c, t, a); // ya corre en boundedElastic
  }
}