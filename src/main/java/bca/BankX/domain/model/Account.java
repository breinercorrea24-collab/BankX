package bca.BankX.domain.model;

import java.math.BigDecimal;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("accounts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    private String id;
    private String number;
    private String holderName;
    private String currency; // "PEN" / "USD"
    private BigDecimal balance;
}