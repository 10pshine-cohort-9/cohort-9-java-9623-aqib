package com.example.contactmanager.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * JPA entity for a persisted refresh token, enabling server-side revocation
 * (logout, password change) per OWASP JWT revocation best practices.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "refresh_tokens", uniqueConstraints = {
        @UniqueConstraint(name = "uk_refresh_token_jti", columnNames = "jti")
})
public class RefreshToken extends BaseEntity {

    @Setter
    @Column(name = "jti", nullable = false, length = 64, updatable = false)
    private String jti;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @Setter(AccessLevel.PROTECTED)
    private User user;

    @Setter
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Setter
    @Column(name = "revoked", nullable = false)
    @Builder.Default
    private boolean revoked = false;
}
