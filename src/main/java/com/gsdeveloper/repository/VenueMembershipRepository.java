package com.gsdeveloper.bookmyslot.repository;
import com.gsdeveloper.bookmyslot.entity.VenueMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface VenueMembershipRepository extends JpaRepository<VenueMembership, UUID> { List<VenueMembership> findByVenueId(UUID venueId); }
