package com.idealstudy.eureka.client.gateway.filter;

import com.idealstudy.eureka.client.gateway.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * JWT 검증하고 헤더에 유저정보 껴서 라우팅할 서비스의 헤더에 껴주는 필터
 * (굳이 인증인가가 필요없는 서비스가 있을 수 있으니 글로벌 필터가 아닌 게이트웨이 필터를 선택)
 */
@Slf4j(topic = "jwtAuthGatewayFilter")
@RequiredArgsConstructor
@Component
public class JwtAuthGatewayFilter implements GatewayFilter {

    private final JwtUtil jwtUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders(); // 헤더
        log.info("headers {}",headers);
        String token = headers.getFirst(HttpHeaders.AUTHORIZATION); // 헤더의 토큰
        log.info("token {}",token);
        Claims claims = jwtUtils.isTokenValid(token);
        log.info("claims {}",claims); // payload 임 payload 가 claim 들의 집합임

        // JWT 토큰 검증
        if (token != null && claims != null) {
            String username = jwtUtils.getUsernameFromToken(token); // 유저이름 추출 getSubject()
            String roles = jwtUtils.getRoleFromToken(token); // 유저권한 추출 get(AUTHORIZATION_KEY)

            log.info("추출한 토큰의 username = {}, , roles = {}, affiliationType = {}, affiliationId = {}", username,roles); // 토큰 payload(claims) 다 가져오기

            exchange.getRequest().mutate()
                .header("X-User-Name", username)
                .header("X-User-Role", roles)
                .build();

            return chain.filter(exchange);
        }

        return Mono.error(new RuntimeException("Invalid Token"));
    }

}
