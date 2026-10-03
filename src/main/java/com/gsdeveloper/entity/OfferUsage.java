package com.gsdeveloper.bookmyslot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "offer_usage", uniqueConstraints = @UniqueConstraint(name = "uk_offer_user_booking", columnNames = {"offer_id", "user_id", "booking_id"}))
@Getter @Setter @NoArgsConstructor
public class OfferUsage {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "offer_id") private Offer offer;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "user_id") private User user;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "booking_id") private Booking booking;
    @Column(name = "discount_amount", nullable = false, precision = 12, scale = 2) private BigDecimal discountAmount;
    @Column(name = "used_at", nullable = false, updatable = false) private LocalDateTime usedAt;
    @PrePersist void onCreate() { usedAt = LocalDateTime.now(); }
}
