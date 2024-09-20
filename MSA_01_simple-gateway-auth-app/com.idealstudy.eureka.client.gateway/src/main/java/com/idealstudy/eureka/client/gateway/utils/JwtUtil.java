package com.idealstudy.eureka.client.gateway.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// jwt 권한 필터 만들때 필요한 설정
@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {

    public static final String AUTHORIZATION_KEY = "auth"; // JWT 내의 사용자 권한 값의 KEY
    public static final String BEARER_PREFIX = "Bearer "; // Token 식별자

    @Value("${jwt.secret.key}")
    private String JWT_SECRET_KEY; // JWT 생성 및 검증에 사용할 비밀키 (Base 64로 Encode)
    private SecretKey secretKey; // JWT 생성 및 검증에 사용할 Key 인터페이스를 구현한 객체
    private JwtParser jwtParser; // JWT 검증을 위한 JwtParser 객체 (유효성 검사 및 Payload 추출)

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(JWT_SECRET_KEY); // secretKey -> Base64 디코딩하여 byte 배열로 저장
        secretKey = Keys.hmacShaKeyFor(bytes);
        jwtParser = Jwts.parser().verifyWith(secretKey).build();
    }

    /**
     * 토큰 유효성 검증 -> jwtParser 로 대체
     */
    public Claims isTokenValid(String token) {
        try {
            return getUserInfoFromToken(token);
        } catch (SignatureException | ExpiredJwtException e) {
            // 서명 오류 또는 만료된 토큰인 경우
            return null;
        }
    }

    // 사실 필요 없는
    private String createToken(String nickname, String role, long expiration) {
        Date now = new Date();

        return Jwts.builder()
            .subject(nickname)
            .claim(AUTHORIZATION_KEY, role)
            .expiration(new Date(now.getTime() + expiration))
            .issuedAt(now)
            .signWith(secretKey, SIG.HS512)
            .compact();
    }

    /**
     * 토큰에서 username 가져오기
     */
    public String getUsernameFromToken(String token) {
        return getUserInfoFromToken(token).getSubject();
    }

    /**
     * 토큰에서 role 가져오기
     */
    public String getRoleFromToken(String token) {
        return (String) getUserInfoFromToken(token).get(AUTHORIZATION_KEY);
    }

    /**
     * 토큰에서 affiliationType 가져오기
     */
    public String getAffiliationTypeFromToken(String token) {
        return (String) getUserInfoFromToken(token).get("AffiliationType");
    }

    /**
     * 토큰에서 affiliationId 가져오기
     */
    public String getAffiliationIdFromToken(String token) {
        return (String) getUserInfoFromToken(token).get("AffiliationId");
    }

    /**
     * 토큰에서 남은 시간을 초 단위로 가져오기
     */
    public int getTtlInSecondsFromToken(String token) {
        Date expiration = getUserInfoFromToken(token).getExpiration();

        int expirationTimeInSeconds = (int) (expiration.getTime() / 1000);
        int nowTimeInSeconds = (int) (new Date().getTime() / 1000);

        return expirationTimeInSeconds - nowTimeInSeconds;
    }

    /**
     * 토큰 payload(claims) 다 가져오기
     */
    public Claims getPayloadFromToken(String token) {
        return getUserInfoFromToken(token);
    }

    /**
     * 토큰에서 payload (claim 들의 집합) 가져오기
     */
    private Claims getUserInfoFromToken(String token) {
        return jwtParser.parseSignedClaims(token).getPayload();
    }
}
