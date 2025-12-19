package bca.bankx.infrastructure.logging;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Log context utility.
 */
@Component
public class LogContext {
  /**
   * Wraps a Mono with MDC context.
   *
   * @param mono the mono to wrap
   * @param <T> the type
   * @return the wrapped mono
   */
  public <T> Mono<T> withMdc(Mono<T> mono) {
    return Mono.deferContextual(ctx -> {
      System.out.println("LogContext - Context: " + ctx);
      String corr = ctx.getOrDefault("corrId", "na").toString();
      ThreadContext.put("corrId", corr);
      return mono.doFinally(sig -> ThreadContext.remove("corrId"));
    });
  }
}
