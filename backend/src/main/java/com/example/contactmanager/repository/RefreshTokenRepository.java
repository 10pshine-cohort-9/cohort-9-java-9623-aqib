package com.example.contactmanager.repository;

import com.example.contactmanager.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link RefreshToken} entities, supporting
 * lookup by JWT ID and bulk revocation by user.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Finds a refresh token by its JWT ID (jti claim).
     *
     * @param jti the JWT ID to search for
     * @return an Optional containing the refresh token if found
     */
    Optional<RefreshToken> findByJti(String jti);

    /**
     * Revokes all refresh tokens belonging to a user (used on password change).
     *
     * @param userId the owner's id
     * @return the number of tokens revoked
     */
    @Modifying
    @Query("update RefreshToken r set r.revoked = true where r.user.id = :userId and r.revoked = false")
    int revokeAllByUserId(@Param("userId") Long userId);
}
