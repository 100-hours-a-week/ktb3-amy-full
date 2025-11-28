package kr.adapterz.amy_community.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    // 최소 32바이트 이상 키 필수
    private static final String SECRET =
            "your-very-secure-secret-key-12345678901234567890";

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    // Access Token: 30분
    private static final long ACCESS_EXPIRE = 1000L * 60 * 30;

    // Refresh Token: 14일
    private static final long REFRESH_EXPIRE = 1000L * 60 * 60 * 24 * 14;

    public String generateAccessToken(Long userId) {
        return createToken(userId, ACCESS_EXPIRE);
    }

    public String generateRefreshToken(Long userId) {
        return createToken(userId, REFRESH_EXPIRE);
    }

    private String createToken(Long userId, long expire) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(new Date(now))
                .expiration(new Date(now + expire))
                .signWith(key)
                .compact();
    }

    public Long validateAccessToken(String token) {
        return validateAndGetUserId(token);
    }

    public Long validateRefreshToken(String token) {
        return validateAndGetUserId(token);
    }

    private Long validateAndGetUserId(String token) {
        try {
            Claims claims = Jwts
                    .parser()
                    .verifyWith(key)           // 서명키 설정
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return Long.valueOf(claims.getSubject());

        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(null, null, "expired_token");
        } catch (SignatureException e) {
            throw new IllegalArgumentException("invalid_signature");
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid_token");
        }
    }
}