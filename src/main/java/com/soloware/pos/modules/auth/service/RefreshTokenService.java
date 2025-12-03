package com.soloware.pos.modules.auth.service;

import com.soloware.pos.modules.auth.entity.RefreshTokenEntity;
import com.soloware.pos.modules.auth.entity.UserEntity;
import com.soloware.pos.modules.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration}")
    private Long refreshTokenDurationMs;

    @Transactional
    public RefreshTokenEntity createRefreshToken(UserEntity user) {
        // Revoke all existing tokens for this user (optional - single device login)
        // refreshTokenRepository.revokeAllUserTokens(user);

        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusSeconds(refreshTokenDurationMs / 1000))
                .revoked(false)
                .build();

        refreshToken = refreshTokenRepository.save(refreshToken);
        log.info("Refresh token created for user: {} (ID: {})", user.getUsername(), user.getId());

        return refreshToken;
    }

    @Transactional
    public RefreshTokenEntity verifyExpiration(RefreshTokenEntity token) {
        if (token.isExpired()) {
            log.warn("Refresh token expired for user: {}", token.getUser().getUsername());
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new login request");
        }

        if (token.getRevoked()) {
            log.warn("Refresh token was revoked for user: {}", token.getUser().getUsername());
            throw new RuntimeException("Refresh token was revoked. Please make a new login request");
        }

        return token;
    }

    @Transactional(readOnly = true)
    public RefreshTokenEntity findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("Refresh token not found: {}", token);
                    return new RuntimeException("Refresh token not found");
                });
    }

    @Transactional
    public void revokeUserTokens(UserEntity user) {
        log.info("Revoking all tokens for user: {} (ID: {})", user.getUsername(), user.getId());
        refreshTokenRepository.revokeAllUserTokens(user);
    }

    @Transactional
    public void deleteUserTokens(UserEntity user) {
        log.info("Deleting all tokens for user: {} (ID: {})", user.getUsername(), user.getId());
        refreshTokenRepository.deleteAllByUser(user);
    }

    @Transactional
    public void cleanupExpiredTokens() {
        log.info("Cleaning up expired and revoked refresh tokens");
        refreshTokenRepository.deleteExpiredAndRevokedTokens();
    }
}

