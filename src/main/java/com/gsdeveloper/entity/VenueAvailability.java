package com.gsdeveloper.bookmyslot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "venue_availability", indexes = @Index(name = "idx_availability_court_day", columnList = "court_id,day_of_week"))
@Getter @Setter @NoArgsConstructor
public class VenueAvailability {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "court_id") private VenueCourt court;
    @Enumerated(EnumType.STRING) @Column(name = "day_of_week", nullable = false) private DayOfWeek dayOfWeek;
    @Column(name = "start_time", nullable = false) private LocalTime startTime;
    @Column(name = "end_time", nullable = false) private LocalTime endTime;
    @Column(nullable = false) private boolean active = true;
}
