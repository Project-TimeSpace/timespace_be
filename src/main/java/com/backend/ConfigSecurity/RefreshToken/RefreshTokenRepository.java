package com.backend.ConfigSecurity.RefreshToken;

import com.backend.User.Entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUser(User user);
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}

