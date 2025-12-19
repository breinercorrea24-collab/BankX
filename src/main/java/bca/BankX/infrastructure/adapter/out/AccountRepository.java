package bca.bankx.infrastructure.adapter.out;

import bca.bankx.domain.model.Account;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 * Repository for Account entities.
 */
public interface AccountRepository extends ReactiveMongoRepository<Account, String> {
  Mono<Account> findByNumber(String number);
}