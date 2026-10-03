package com.gsdeveloper.bookmyslot.repository;
import com.gsdeveloper.bookmyslot.entity.VenueCourt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface VenueCourtRepository extends JpaRepository<VenueCourt, UUID> { List<VenueCourt> findByVenueIdAndActiveTrue(UUID venueId); }
