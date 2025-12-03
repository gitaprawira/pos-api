package com.soloware.pos.modules.auth.repository;

import com.soloware.pos.modules.auth.entity.RefreshTokenEntity;
import com.soloware.pos.modules.auth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);

    @Modifying
    @Query("UPDATE RefreshTokenEntity rt SET rt.revoked = true WHERE rt.user = :user")
    void revokeAllUserTokens(UserEntity user);

    @Modifying
    @Query("DELETE FROM RefreshTokenEntity rt WHERE rt.user = :user")
    void deleteAllByUser(UserEntity user);

    @Modifying
    @Query("DELETE FROM RefreshTokenEntity rt WHERE rt.expiryDate < CURRENT_TIMESTAMP OR rt.revoked = true")
    void deleteExpiredAndRevokedTokens();
}

