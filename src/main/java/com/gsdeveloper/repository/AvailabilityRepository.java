package com.gsdeveloper.bookmyslot.repository;
import com.gsdeveloper.bookmyslot.entity.VenueAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface AvailabilityRepository extends JpaRepository<VenueAvailability, UUID> { List<VenueAvailability> findByCourtIdAndActiveTrue(UUID courtId); }
