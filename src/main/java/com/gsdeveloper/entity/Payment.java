package com.gsdeveloper.bookmyslot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments", indexes = @Index(name = "idx_payment_booking", columnList = "booking_id"))
@Getter @Setter @NoArgsConstructor
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @OneToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "booking_id", unique = true) private Booking booking;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal amount;
    @Column(nullable = false) private String currency = "INR";
    @Column(nullable = false) private String status = "PENDING";
    @Column(name = "provider", nullable = false) private String provider;
    @Column(name = "provider_payment_id", unique = true) private String providerPaymentId;
    @Column(name = "provider_order_id", unique = true) private String providerOrderId;
    @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false) private LocalDateTime updatedAt;
    @PrePersist void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedAt = LocalDateTime.now(); }
}
