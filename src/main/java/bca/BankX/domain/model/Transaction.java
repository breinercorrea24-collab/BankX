package bca.BankX.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    private String id;
    private String accountId;
    private String type; // "CREDIT" or "DEBIT"
    private BigDecimal amount;
    private Instant timestamp;
    private String status; // "OK" or "REJECTED"
    private String reason; // null if OK
}