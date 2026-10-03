package com.gsdeveloper.bookmyslot.repository;
import com.gsdeveloper.bookmyslot.entity.VenueRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface VenueRegistrationRepository extends JpaRepository<VenueRegistration, UUID> { }
