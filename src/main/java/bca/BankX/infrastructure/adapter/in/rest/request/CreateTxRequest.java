package bca.bankX.infrastructure.adapter.in.rest.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

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