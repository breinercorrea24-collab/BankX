package bca.bankx.infrastructure.adapter.in.rest;

import bca.bankx.application.service.TransactionService;
import bca.bankx.domain.model.Transaction;
import bca.bankx.infrastructure.adapter.in.rest.request.CreateTxRequest;
import bca.bankx.infrastructure.logging.LogContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * REST controller for transaction operations.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TransactionController {
  private final TransactionService service;
  private final LogContext logContext;
  private static final Logger log = LoggerFactory.getLogger(TransactionController.class);

  /**
   * Creates a new transaction.
   *
   * @param req the create transaction request
   * @return Mono of ResponseEntity with Transaction
   */
  @PostMapping("/transactions")
  public Mono<ResponseEntity<Transaction>> create(@Valid @RequestBody CreateTxRequest req) {
    log.debug("Creating tx {}", req);
    System.out.println("Creating tx " + req);

    return logContext.withMdc(
        service.create(req)
            .map(tx ->
                ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(tx)
            )
    ).doOnSuccess(response -> {
      System.out.println("Tx created: " + response.getBody());
      log.info("tx_created account={} amount={}",
          req.getAccountNumber(),
          req.getAmount());
    });
    // return service.create(req).map(t -> ResponseEntity.status(HttpStatus.CREATED).body(t));
  }

  /**
   * Lists transactions by account.
   *
   * @param accountNumber the account number
   * @return Flux of Transaction
   */
  @GetMapping("/transactions")
  public Flux<Transaction> list(@RequestParam String accountNumber) {
    return service.byAccount(accountNumber);
  }

  /**
   * Streams transactions.
   *
   * @return Flux of ServerSentEvent
   */
  @GetMapping(value = "/stream/transactions", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<ServerSentEvent<Transaction>> stream() {
    return service.stream();
  }
}