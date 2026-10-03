package com.gsdeveloper.bookmyslot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "venue_memberships", uniqueConstraints = @UniqueConstraint(name = "uk_venue_membership", columnNames = {"venue_id", "membership_id"}))
@Getter @Setter @NoArgsConstructor
public class VenueMembership {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "venue_id") private Venue venue;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "membership_id") private Membership membership;
    @Column(name = "starts_on", nullable = false) private LocalDate startsOn;
    @Column(name = "ends_on", nullable = false) private LocalDate endsOn;
    @Column(nullable = false) private String status = "ACTIVE";
    @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;
    @PrePersist void onCreate() { createdAt = LocalDateTime.now(); }
}
