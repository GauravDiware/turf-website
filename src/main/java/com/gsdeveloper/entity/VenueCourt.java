package com.gsdeveloper.bookmyslot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "venue_courts", indexes = @Index(name = "idx_court_venue", columnList = "venue_id"))
@Getter @Setter @NoArgsConstructor
public class VenueCourt {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "venue_id") private Venue venue;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "sport_id") private Sport sport;
    @Column(nullable = false) private String name;
    private String description;
    private Integer capacity;
    private String surface;
    @Column(name = "price_per_hour", nullable = false, precision = 12, scale = 2) private BigDecimal pricePerHour;
    @Column(nullable = false) private boolean active = true;
}
