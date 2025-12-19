package bca.bankX.infrastructure.adapter.out;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import bca.bankX.domain.model.RiskRule;

public interface RiskRuleRepository extends JpaRepository<RiskRule, Long> {
    Optional<RiskRule> findFirstByCurrency(String currency);
}