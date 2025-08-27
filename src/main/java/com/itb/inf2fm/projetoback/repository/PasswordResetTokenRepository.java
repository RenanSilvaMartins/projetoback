package com.itb.inf2fm.projetoback.repository;

import com.itb.inf2fm.projetoback.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    Optional<PasswordResetToken> findByTokenAndEmailAndUsedFalse(String token, String email);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken p WHERE p.expiryDate < :now OR p.used = true")
    void deleteExpiredAndUsedTokens(LocalDateTime now);
    
    @Modifying
    @Transactional
    @Query("UPDATE PasswordResetToken p SET p.used = true WHERE p.email = :email AND p.used = false")
    void invalidateAllTokensForEmail(String email);
}