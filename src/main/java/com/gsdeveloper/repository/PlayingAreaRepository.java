package com.gsdeveloper.bookmyslot.repository;
import com.gsdeveloper.bookmyslot.entity.PlayingArea;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface PlayingAreaRepository extends JpaRepository<PlayingArea, UUID> {
    List<PlayingArea> findByVenueIdAndActiveTrue(UUID venueId);
    Optional<PlayingArea> findByIdAndVenueOwnerEmail(UUID id, String email);
    @Query("select a from PlayingArea a join fetch a.sport where a.venue.id in :venueIds and a.active = true and a.sport.active = true")
    List<PlayingArea> findPublicAreasByVenueIds(@Param("venueIds") List<UUID> venueIds);
    @Query("select distinct a.sport.name from PlayingArea a join a.sport s where a.venue.status in :statuses and a.active = true and s.active = true")
    List<String> findPublicSportNames(@Param("statuses") List<com.gsdeveloper.bookmyslot.enums.VenueStatus> statuses);
}
