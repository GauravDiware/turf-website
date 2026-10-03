package com.gsdeveloper.bookmyslot.repository;

import com.gsdeveloper.bookmyslot.entity.Venue;
import com.gsdeveloper.bookmyslot.enums.VenueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VenueRepository extends JpaRepository<Venue, UUID> {
    List<Venue> findByOwnerEmail(String email);
    Optional<Venue> findByIdAndOwnerEmail(UUID id, String email);
    List<Venue> findByStatusIn(List<VenueStatus> statuses);
    long countByStatus(VenueStatus status);

    /** Public search is performed by the database against visible venues and active areas. */
    @Query("""
            select v from Venue v
            where v.status in :statuses
              and (cast(:city as string) is null or lower(v.city) = lower(cast(:city as string)))
              and (cast(:game as string) is null or exists (select registeredSport from Venue matchedVenue join matchedVenue.registeredSports registeredSport where matchedVenue = v and lower(registeredSport) = lower(cast(:game as string))))
              and (cast(:search as string) is null or lower(v.name) like lower(concat('%', cast(:search as string), '%')) or lower(v.city) like lower(concat('%', cast(:search as string), '%')) or lower(v.address) like lower(concat('%', cast(:search as string), '%'))
                   or exists (select registeredSport from Venue matchedVenue join matchedVenue.registeredSports registeredSport where matchedVenue = v and lower(registeredSport) like lower(concat('%', cast(:search as string), '%'))))
            order by case when :sort = 'price-low' then (select min(a.basePrice) from PlayingArea a where a.venue = v and a.active = true) end asc,
                     case when :sort = 'price-high' then (select min(a.basePrice) from PlayingArea a where a.venue = v and a.active = true) end desc,
                     case when :sort = 'name' then lower(v.name) end asc,
                     case when :sort = 'location' then lower(v.city) end asc,
                     lower(v.name) asc
            """)
    List<Venue> searchPublicVenues(@Param("search") String search, @Param("city") String city, @Param("game") String game,
                                   @Param("sort") String sort, @Param("statuses") List<VenueStatus> statuses);

    @Query("""
            select v.city as city, count(v) as turfCount from Venue v
            where v.status in :statuses and v.city is not null and trim(v.city) <> ''
            group by v.city
            """)
    List<CityTurfCount> findPublicCityCounts(@Param("statuses") List<VenueStatus> statuses);

    @Query("""
            select distinct registeredSport from Venue v join v.registeredSports registeredSport
            where v.status in :statuses and trim(registeredSport) <> ''
            """)
    List<String> findPublicRegisteredSports(@Param("statuses") List<VenueStatus> statuses);
}
