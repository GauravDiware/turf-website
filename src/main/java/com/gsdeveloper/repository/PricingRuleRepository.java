package com.gsdeveloper.bookmyslot.repository;
import com.gsdeveloper.bookmyslot.entity.PricingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface PricingRuleRepository extends JpaRepository<PricingRule, UUID> { List<PricingRule> findByPlayingAreaIdAndActiveTrue(UUID playingAreaId); }
