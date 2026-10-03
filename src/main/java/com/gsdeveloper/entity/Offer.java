package com.gsdeveloper.bookmyslot.entity;

import com.gsdeveloper.bookmyslot.enums.DiscountType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity @Table(name = "offers", indexes = @Index(name = "idx_offer_coupon", columnList = "couponCode", unique = true)) @Getter @Setter @NoArgsConstructor
public class Offer {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(optional = false) @JoinColumn(name = "venue_id") private Venue venue;
    @Column(nullable = false, unique = true) private String couponCode;
    @Column(nullable = false) private String name;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private DiscountType discountType;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal discountValue;
    private LocalDate startDate; private LocalDate endDate; private LocalTime startTime; private LocalTime endTime;
    private BigDecimal minimumBookingAmount; private BigDecimal maximumDiscount;
    private Integer usageLimit; private Integer perCustomerLimit;
    @Column(nullable = false) private boolean active = true;
}
