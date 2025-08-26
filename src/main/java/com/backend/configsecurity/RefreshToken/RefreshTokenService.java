package com.backend.configsecurity.RefreshToken;

import com.backend.configsecurity.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {


    private final StringRedisTemplate redis;
    private final JwtTokenProvider jwtProvider;

    public void storeToken(String refreshToken) {
        String jti = jwtProvider.getJti(refreshToken);
        long ttl = jwtProvider.getExpirationDate(refreshToken).getTime() - System.currentTimeMillis();
        redis.opsForValue()
            .set("refresh:" + jti, refreshToken, ttl, TimeUnit.MILLISECONDS);
    }

    public boolean exists(String refreshToken) {
        String jti = jwtProvider.getJti(refreshToken);
        return Boolean.TRUE.equals(redis.hasKey("refresh:" + jti));
    }

    public void delete(String refreshToken) {
        String jti = jwtProvider.getJti(refreshToken);
        redis.delete("refresh:" + jti);
    }

    public void rotate(String oldToken, String newToken) {
        String oldJti = jwtProvider.getJti(oldToken);
        redis.delete("refresh:" + oldJti);
        storeToken(newToken);
    }

    /* 기존 DB기반 리프레시 토큰 Service
    // final RefreshTokenRepository refreshTokenRepository;

    public void saveOrUpdateToken(user user, String token, LocalDateTime expiry) {
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

    public void deleteByUser(user user) {
        refreshTokenRepository.deleteByUser(user);
    }
    */



}
