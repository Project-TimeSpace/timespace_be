package com.backend.ConfigSecurity.RefreshToken;

import com.backend.User.Entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public void saveOrUpdateToken(User user, String token, LocalDateTime expiry) {
        refreshTokenRepository.findByUser(user).ifPresentOrElse(
                existing -> {
                    existing.setToken(token);
                    existing.setExpiryDate(expiry);
                    refreshTokenRepository.save(existing);
                },
                () -> {
                    RefreshToken newToken = new RefreshToken();
                    newToken.setUser(user);
                    newToken.setToken(token);
                    newToken.setExpiryDate(expiry);
                    refreshTokenRepository.save(newToken);
                }
        );
    }

    public RefreshToken getByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
    }

    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
