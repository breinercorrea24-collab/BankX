package bca.bankX.application.service;

import java.math.BigDecimal;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RiskRemoteClient {

    @Autowired
    RiskService legacy; // el de JPA que ya tenías

    private final WebClient riskWebClient;

    @Retry(name = "riskClient")
    @CircuitBreaker(name = "riskClient", fallbackMethod = "fallback")
    public Mono<Boolean> isAllowed(String currency, String type, BigDecimal amount) {
        System.out.println("RiskRemoteClient - Checking risk for currency=" + currency + ", type=" + type + ", amount=" + amount);
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

    // Fallback → usa módulo legado JPA (bloqueante envuelto)
    public Mono<Boolean> fallback(String currency, String type, BigDecimal amount,
            Throwable ex) {
        return legacyAllowed(currency, type, amount);
    }
    

    private Mono<Boolean> legacyAllowed(String c, String t, BigDecimal a) {
        return legacy.isAllowed(c, t, a); // ya corre en boundedElastic
    }
}