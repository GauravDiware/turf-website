package com.gsdeveloper.bookmyslot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@Entity @Table(name = "playing_areas", indexes = @Index(name = "idx_area_venue", columnList = "venue_id")) @Getter @Setter @NoArgsConstructor
public class PlayingArea {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(optional = false) @JoinColumn(name = "venue_id") private Venue venue;
    @ManyToOne(optional = false) @JoinColumn(name = "sport_id") private Sport sport;
    @Column(nullable = false) private String name;
    private String description;
    private Integer capacity;
    private String surfaceType;
    private String indoorOutdoor;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal basePrice;
    @Column(nullable = false) private boolean active = true;
}
