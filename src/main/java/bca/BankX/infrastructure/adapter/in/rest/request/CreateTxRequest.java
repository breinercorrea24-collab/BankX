package bca.bankx.infrastructure.adapter.in.rest.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

/**
 * Request DTO for creating a transaction.
 */
@Data
public class CreateTxRequest {
  @NotBlank
  private String accountNumber;
  @NotBlank
  private String type; // CREDIT/DEBIT
  @NotNull
  @DecimalMin("0.01")
  private BigDecimal amount;
}