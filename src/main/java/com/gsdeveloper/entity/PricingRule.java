package com.gsdeveloper.bookmyslot.entity;

import com.gsdeveloper.bookmyslot.enums.PricingRuleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity @Table(name = "pricing_rules", indexes = @Index(name = "idx_price_area", columnList = "playing_area_id")) @Getter @Setter @NoArgsConstructor
public class PricingRule {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(optional = false) @JoinColumn(name = "venue_id") private Venue venue;
    @ManyToOne(optional = false) @JoinColumn(name = "playing_area_id") private PlayingArea playingArea;
    @Enumerated(EnumType.STRING) private DayOfWeek dayOfWeek;
    private LocalDate specificDate;
    @Column(nullable = false) private LocalTime startTime;
    @Column(nullable = false) private LocalTime endTime;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal price;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private PricingRuleType ruleType;
    @Column(nullable = false) private Integer priority;
    @Column(nullable = false) private boolean active = true;
}
