package bca.bankx.infrastructure.adapter.out;

import bca.bankx.domain.model.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

/**
 * Repository for Transaction entities.
 */
public interface TransactionRepository extends
    ReactiveMongoRepository<Transaction, String> {
  Flux<Transaction> findByAccountIdOrderByTimestampDesc(String accountId);
}