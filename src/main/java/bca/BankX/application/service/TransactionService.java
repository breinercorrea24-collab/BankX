package bca.bankX.application.service;

import java.math.BigDecimal;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;

import bca.bankX.infrastructure.adapter.in.rest.request.CreateTxRequest;
import bca.bankX.domain.exception.BusinessException;
import bca.bankX.domain.model.Account;
import bca.bankX.domain.model.Transaction;
import bca.bankX.infrastructure.adapter.out.AccountRepository;
import bca.bankX.infrastructure.adapter.out.TransactionRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);
    private final AccountRepository accountRepo;
    private final TransactionRepository txRepo;
    private final RiskRemoteClient riskRemoteClient;
    private final Sinks.Many<Transaction> txSink;

    public Mono<Transaction> create(CreateTxRequest req) {
        System.out.println("TransactionService - Creating transaction for request: " + req);
        return accountRepo.findByNumber(req.getAccountNumber())
                .doOnNext(acc -> System.out.println("TransactionService - Found account in MongoDB: " + acc))
                .switchIfEmpty(Mono.defer(() -> {
                    System.out.println("Account not found in MongoDB: " + req.getAccountNumber());
                    return Mono.error(new BusinessException("account_not_found"));
                }))
                .flatMap(acc -> validateAndApply(acc, req))
                .onErrorMap(IllegalStateException.class, e -> new BusinessException(e.getMessage()))
                .doOnError(e -> System.out.println("Error creating transaction: " + e.getMessage()));
    }

    private Mono<Transaction> validateAndApply(Account acc, CreateTxRequest req) {
        log.debug("TransactionService - Validating and applying transaction for account: {}", acc.getNumber());
        String type = req.getType().toUpperCase();
        BigDecimal amount = req.getAmount();

        return riskRemoteClient.isAllowed(acc.getCurrency(), type, amount)
                .flatMap(allowed -> {
                    if (!allowed) return Mono.error(new BusinessException("risk_rejected"));
                    if ("DEBIT".equals(type) && acc.getBalance().compareTo(amount) < 0) {
                        return Mono.error(new BusinessException("insufficient_funds"));
                    }

                    return Mono.just(acc)
                            .publishOn(Schedulers.parallel())
                            .map(a -> {
                                BigDecimal newBal = "DEBIT".equals(type) ?
                                        a.getBalance().subtract(amount) : a.getBalance().add(amount);
                                a.setBalance(newBal);
                                return a;
                            })
                            .flatMap(accountRepo::save)
                            .flatMap(saved -> txRepo.save(Transaction.builder()
                                    .accountId(saved.getId())
                                    .type(type)
                                    .amount(amount)
                                    .timestamp(Instant.now())
                                    .status("OK")
                                    .build()))
                            .doOnNext(tx -> txSink.tryEmitNext(tx));
                });
    }

    public Flux<Transaction> byAccount(String accountNumber) {
        return accountRepo.findByNumber(accountNumber)
                .switchIfEmpty(Mono.error(new BusinessException("account_not_found")))
                .flatMapMany(acc -> txRepo.findByAccountIdOrderByTimestampDesc(acc.getId()));
    }

    public Flux<ServerSentEvent<Transaction>> stream() {
        return txSink.asFlux()
                .map(tx -> ServerSentEvent.builder(tx).event("transaction").build());
    }
}