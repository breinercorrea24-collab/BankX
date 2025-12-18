package bca.BankX.infrastructure.adapter.out;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import bca.BankX.domain.model.Account;
import reactor.core.publisher.Mono;

public interface AccountRepository extends ReactiveMongoRepository<Account, String> {
    Mono<Account> findByNumber(String number);
}