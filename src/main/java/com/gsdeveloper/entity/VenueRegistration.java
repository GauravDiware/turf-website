package com.gsdeveloper.bookmyslot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "venue_registrations", indexes = @Index(name = "idx_registration_email", columnList = "email"))
@Getter @Setter @NoArgsConstructor
public class VenueRegistration {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "owner_id") private User owner;
    @Column(nullable = false) private String fullName;
    @Column(nullable = false) private String email;
    @Column(nullable = false) private String mobile;
    @Column(nullable = false) private String venueName;
    @Column(nullable = false, length = 2000) private String address;
    @Column(nullable = false) private String city;
    private String state;
    @Column(nullable = false, length = 6) private String pincode;
    @Column(nullable = false) private String status = "PENDING";
    @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;
    @PrePersist void onCreate() { createdAt = LocalDateTime.now(); }
}
