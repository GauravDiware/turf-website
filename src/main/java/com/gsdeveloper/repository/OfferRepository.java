package com.gsdeveloper.bookmyslot.repository;
import com.gsdeveloper.bookmyslot.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
public interface OfferRepository extends JpaRepository<Offer, UUID> { Optional<Offer> findByCouponCodeIgnoreCase(String couponCode); }
