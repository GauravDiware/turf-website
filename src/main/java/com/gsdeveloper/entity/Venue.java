package com.gsdeveloper.bookmyslot.entity;

import com.gsdeveloper.bookmyslot.enums.VenueStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "venues", indexes = @Index(name = "idx_venue_owner", columnList = "owner_id"))
@Getter @Setter @NoArgsConstructor
public class Venue {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(optional = false) @JoinColumn(name = "owner_id", nullable = false) private User owner;
    @Column(nullable = false) private String name;
    @Column(length = 2000) private String description;
    private String venueType;
    @Column(nullable = false) private String address;
    @Column(nullable = false) private String city;
    private String state;
    @Column(nullable = false, length = 10) private String pincode;
    private String contactNumber;
    private String whatsappNumber;
    private String email;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private String weeklyOff;
    @Column(name = "image_url", length = 2048) private String imageUrl;
    /** Sports chosen by the owner during venue registration. */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "venue_registered_sports", joinColumns = @JoinColumn(name = "venue_id"))
    @Column(name = "sport", nullable = false)
    private Set<String> registeredSports = new LinkedHashSet<>();
    @Enumerated(EnumType.STRING) @Column(nullable = false) private VenueStatus status;
    @Column(nullable = false, updatable = false) private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @PrePersist void created() { createdAt = LocalDateTime.now(); updatedAt = createdAt; }
    @PreUpdate void updated() { updatedAt = LocalDateTime.now(); }
}
