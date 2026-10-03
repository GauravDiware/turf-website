package com.gsdeveloper.bookmyslot.repository;
import com.gsdeveloper.bookmyslot.entity.OfferUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface OfferUsageRepository extends JpaRepository<OfferUsage, UUID> { long countByOfferIdAndUserId(UUID offerId, UUID userId); }
