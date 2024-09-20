package com.idealstudy.eureka.client.auth.security;

import com.idealstudy.eureka.client.auth.common.shared.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * jwt 관련 기능들을 가진 클래스, jwt Util 만드는 중 (특정 파라미터에 대한 작업을 수행하는 메소드들이 있는 곳) (다른 객체에 의존하지 않고 하나의 모듈로써
 * 동작하는 클래스)
 */
@Component
@Slf4j(topic = "JWT 관련 로그")
public class JwtUtil {

    ////////////////////////////// JWT 데이터 /////////////////////////////

    // Header KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // 사용자 권한 값의 KEY
    public static final String AUTHORIZATION_KEY = "auth";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";
    // 토큰 만료시간
//    private final long TOKEN_TIME = 10 * 1000L; // 10초
    private final long TOKEN_TIME = 60 * 60 * 1000L; // 60분
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    private Key key;

    @PostConstruct // 한 번만 받아와도 되는 값을 사용할때마다 요청을 새로하지 않기 위해
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    ////////////////////////////////////////////////////////////////////

    // 1. JWT 토큰 생성 (1) JWT 토큰을 헤더에 달아 보낼수도 있고 (2) 쿠키객체에 담아 줄 수도 있다 - 프론트와 조율해야함
    public String createToken(String username, UserRole role) {
        Date date = new Date();

        return BEARER_PREFIX +
               Jwts.builder()
                       .subject(username) // 사용자 식별자값(ID)
                       .claim(AUTHORIZATION_KEY, role) // 사용자 권한
                       .expiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 시간
                       .issuedAt(date) // 발급일
                       .signWith(key) // 암호화 알고리즘
                       .compact();
    }

    // 2. jWT를 쿠키에 저장
    public void addJwtToCookie(String token, HttpServletResponse res) {
        try {
            // TODO : utf-8을 하드코딩 하기보다 StandardCharsets.UTF_8.name() 를 사용하면 좋을 것 같습니다!
            token = URLEncoder.encode(token, "utf-8")
                    .replaceAll("\\+", "%20"); // Cookie Value 에는 공백이 불가능해서 encoding 진행

            Cookie cookie = new Cookie(AUTHORIZATION_HEADER, token); // Name-Value
            cookie.setPath("/");
            // 쿠키에 만료시간 추가
            cookie.setMaxAge((int) (TOKEN_TIME / 1000L));
            cookie.setHttpOnly(true); // XSS 방지

            // Response 객체에 Cookie 추가
            res.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
        }
    }

    // 3. Cookie 의 Value에 있던 JWT 토큰을 Substring(자른다)
    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }
        log.error("Not Found Token");
        throw new NullPointerException("Not Found Token");
    }

    // 4. JWT 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    // 5. JWT에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    // HttpServletRequest 에서 Cookie Value : JWT 가져오기
    public String getTokenFromRequest(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        // TODO : early return 같은 패턴으로 depth를 줄여보는 것도 좋을 것 같습니다!
        // TODO : 설정 -> tools -> save in action 에서 위의 3개 (코드 서식 다시 지정, import 문 최적화, 코드 재정렬) 체크하고 적용하면 포맷이 알아서 맞춰집니다!
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(AUTHORIZATION_HEADER)) {
                    try {
                        return URLDecoder.decode(cookie.getValue(),
                                "UTF-8"); // Encode 되어 넘어간 Value 다시 Decode
                    } catch (UnsupportedEncodingException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }
}

