package com.example.User.repository;

import com.example.User.entities.RefreshToken;
import com.example.User.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    /**
     * Finds a refresh token by its token string.
     *
     * @param token the refresh token string
     * @return an Optional containing the RefreshToken if found, or empty if not found
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Finds a refresh token by user.
     *
     * @param user the user entity
     * @return an Optional containing the RefreshToken if found, or empty if not found
     */
    Optional<RefreshToken> findByUser(User user);

    /**
     * Deletes refresh tokens that are either:
     * - revoked for more than 30 days, or
     * - expired for more than 30 days.
     *
     * @param threshold date-time threshold (now - 30 days)
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt " +
            "WHERE (rt.revoked = true AND rt.revokedAt < :threshold) " +
            "   OR (rt.expiryDate < :threshold)")
    void deleteOldTokens(@Param("threshold") LocalDateTime threshold);
}

