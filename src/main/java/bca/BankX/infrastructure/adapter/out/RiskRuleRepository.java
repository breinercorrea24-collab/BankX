package bca.bankx.infrastructure.adapter.out;

import bca.bankx.domain.model.RiskRule;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for RiskRule entities.
 */
public interface RiskRuleRepository extends JpaRepository<RiskRule, Long> {
  Optional<RiskRule> findFirstByCurrency(String currency);
}