package com.gsdeveloper.bookmyslot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "profiles")
@Getter @Setter @NoArgsConstructor
public class Profile {
    @Id private UUID id;
    @OneToOne(optional = false, fetch = FetchType.LAZY) @MapsId
    @JoinColumn(name = "id") private User user;
    @Column(name = "avatar_url") private String avatarUrl;
    @Column(name = "phone_verified", nullable = false) private boolean phoneVerified;
    @Column(name = "email_verified", nullable = false) private boolean emailVerified;
    @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false) private LocalDateTime updatedAt;
    @PrePersist void onCreate() { createdAt = LocalDateTime.now(); updatedAt = createdAt; }
    @PreUpdate void onUpdate() { updatedAt = LocalDateTime.now(); }
}
