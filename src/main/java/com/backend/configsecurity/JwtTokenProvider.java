package com.backend.configsecurity;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private final UserDetailsService userDetailsService;  // ← 추가

    private final Key secretKey;
    private final long tokenValidity = 1000L * 60 * 60; // 유효시간 60분
    private final long refreshThreshold = 1000L * 60 * 30; // 30분 이하 시 갱신
    private final long refreshTokenValidityInMs = 1000L * 60 * 60 * 24 * 5; // 리프레시 토큰 유효 3일
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
            @Qualifier("customUserDetailsService") UserDetailsService userDetailsService) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes()); // 최신 Key 생성 방식
        this.userDetailsService = userDetailsService;
    }

    // JWT Access 토큰 생성
    public String createToken(Long id, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tokenValidity);

        return Jwts.builder()
            .setSubject(String.valueOf(id))
            .claim("type", role)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(secretKey, SignatureAlgorithm.HS512)
            .compact();
    }

    /** Refresh Token 생성: JTI(고유 ID) 부여 */
    public String createRefreshToken(Long id, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenValidityInMs);
        String jti = UUID.randomUUID().toString();

        return Jwts.builder()
            .setSubject(String.valueOf(id))
            .claim("type", role)
            .setId(jti) // Refresh만 JTI를 가짐
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(secretKey, SignatureAlgorithm.HS512)
            .compact();
    }

    public String getJti(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getId();
    }

    // 토큰에서 ID 추출
    public Long getIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.valueOf(claims.getSubject());
    }
    // 토큰에서 User인지 Admin인지 구분
    public String getTypeFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("type", String.class);
    }

    public boolean isRefreshToken(String token) {
        return getJti(token) != null;   // jti가 있으면 Refresh
    }

    // JWT 유효성 검증
    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .setAllowedClockSkewSeconds(30)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Date expiration = claims.getExpiration();
            Date now = new Date();

            // 만료까지 30분 이하 남으면 갱신 필요
            return expiration.getTime() - now.getTime() >= refreshThreshold;
        } catch (ExpiredJwtException e) {
            logger.warn("JWT 만료됨: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("지원되지 않는 JWT: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("잘못된 JWT 형식: {}", e.getMessage());
        } catch (SignatureException e) {
            logger.error("JWT 서명 오류: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.warn("JWT가 비어 있음: {}", e.getMessage());
        }
        return false;
    }

    // Authorization 헤더에서 JWT 추출
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Date getExpirationDate(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getExpiration();
    }


}
