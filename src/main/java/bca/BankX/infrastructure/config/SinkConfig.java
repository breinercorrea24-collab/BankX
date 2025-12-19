package bca.bankX.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import bca.bankX.domain.model.Transaction;
import reactor.core.publisher.Sinks;

@Configuration
class SinkConfig {
    @Bean
    public Sinks.Many<Transaction> txSink() {
        return Sinks.many().multicast().onBackpressureBuffer();
    }
}