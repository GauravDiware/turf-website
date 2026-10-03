package com.gsdeveloper.bookmyslot.repository;
import com.gsdeveloper.bookmyslot.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface FavoriteRepository extends JpaRepository<Favorite, UUID> {
    @Query("select f from Favorite f join fetch f.venue where f.user.id = :userId order by f.createdAt desc")
    List<Favorite> findAllByUserIdWithVenue(@Param("userId") UUID userId);

    boolean existsByUserIdAndVenueId(UUID userId, UUID venueId);

    Optional<Favorite> findByUserIdAndVenueId(UUID userId, UUID venueId);

    @Modifying
    @Transactional
    long deleteByUserIdAndVenueId(UUID userId, UUID venueId);
}
