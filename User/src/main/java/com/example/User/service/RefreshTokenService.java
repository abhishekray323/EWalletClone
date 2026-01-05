package com.example.User.service;

import com.example.User.customExceptions.InvalidRefreshTokenException;
import com.example.User.entities.RefreshToken;
import com.example.User.entities.User;
import com.example.User.repository.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshTokenExpirationMs;
    
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                              @Value("${jwt.refresh-expiration-ms}") long refreshTokenExpirationMs) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    /**
     * Creates a new refresh token for the user.
     * If a refresh token already exists for the user, it will be revoked and a new one will be created.
     *
     * @param user the user entity
     * @return the created RefreshToken
     */
    @Transactional
    public String createRefreshToken(User user) {
        // Revoke existing refresh tokens for this user
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser(user);
        if (existingToken.isPresent()) {
            RefreshToken token = existingToken.get();
            token.setRevoked(true);
            token.setRevokedAt(LocalDateTime.now());
            refreshTokenRepository.save(token);
        }

        // Create new refresh token
        String rawToken = UUID.randomUUID().toString();
        String hashedToken = hashToken(rawToken);
        RefreshToken refreshToken = RefreshToken.builder()
                .token(hashedToken)
                .user(user)
                .expiryDate(LocalDateTime.now().plus(Duration.ofMillis(refreshTokenExpirationMs)))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }

    /**
     * Validates a refresh token and returns it if valid.
     *
     * @param token the refresh token string
     * @return the RefreshToken if valid
     * @throws InvalidRefreshTokenException if the token is invalid, expired, or revoked
     */
    @Transactional
    public RefreshToken validateRefreshToken(String token) {
        String hashedToken = hashToken(token);
        LocalDateTime now = LocalDateTime.now();

        try {
            Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByToken(hashedToken);
            if (optionalRefreshToken.isEmpty()) {
                log.warn("Refresh token validation failed: token not found");
                throw new InvalidRefreshTokenException();
            }

            RefreshToken refreshToken = optionalRefreshToken.get();

            if (refreshToken.isRevoked()) {
                log.info("Refresh token validation failed: token revoked (tokenId={}, userId={})",
                        refreshToken.getId(),
                        refreshToken.getUser() != null ? refreshToken.getUser().getId() : null);
                throw new InvalidRefreshTokenException();
            }

            if (refreshToken.getExpiryDate().isBefore(now)) {
                log.info("Refresh token validation failed: token expired (tokenId={}, expiredAt={})",
                        refreshToken.getId(),
                        refreshToken.getExpiryDate());
                throw new InvalidRefreshTokenException();
            }

            return refreshToken;
        } catch (InvalidRefreshTokenException ex) {
            // Re-throw our own exception so that GlobalExceptionHandler can handle it uniformly
            throw ex;
        } catch (Exception ex) {
            // Any unexpected error: log internal details and still return generic message
            log.error("Unexpected error while validating refresh token", ex);
            throw new InvalidRefreshTokenException();
        }
    }

    /**
     * Deletes a refresh token.
     *
     * @param token the refresh token string
     */
    @Transactional
    public void deleteRefreshToken(String token) {
        String hashedToken = hashToken(token);
        refreshTokenRepository.findByToken(hashedToken).ifPresent(refreshTokenRepository::delete);
    }


    
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Cron job that deletes refresh tokens 30 days after they are revoked or expired.
     * Runs every day at 03:00 AM server time.
     */
    @Transactional
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanUpOldRefreshTokens() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        refreshTokenRepository.deleteOldTokens(threshold);
    }


}

