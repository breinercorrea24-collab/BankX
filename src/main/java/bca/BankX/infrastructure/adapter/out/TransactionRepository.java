package bca.BankX.infrastructure.adapter.out;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import bca.BankX.domain.model.Transaction;
import reactor.core.publisher.Flux;

public interface TransactionRepository extends
        ReactiveMongoRepository<Transaction, String> {
    Flux<Transaction> findByAccountIdOrderByTimestampDesc(String accountId);
}