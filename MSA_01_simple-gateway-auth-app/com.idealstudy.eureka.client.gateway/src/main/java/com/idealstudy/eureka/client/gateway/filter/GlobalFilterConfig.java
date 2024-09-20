package com.idealstudy.eureka.client.gateway.filter;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

/**
 * 글로벌 필터로 로깅을 찍어준다
 */
@Configuration
public class GlobalFilterConfig {

    public static final String AUTHORIZATION_KEY = "auth"; // JWT 내의 사용자 권한 값의 KEY
    public static final String BEARER_PREFIX = "Bearer "; // Token 식별자

    // WebFilter, GlobalFilter 모두 Spring WebFlux 환경에서 동작하는 필터임
    // 서블릿 필터와 달리 비동기적으로 요청을 처리

    // WebFlux 애플리케이션 전반에서 사용됩니다. 즉, 모든 HTTP 요청에 대해 동작
    @Bean
    public WebFilter customGlobalFilter() {

        return (exchange, chain) -> {
            System.out.println("WebFilter: Request received at " + exchange.getRequest().getURI());
            //System.out.println(createToken("testUser","MASTER",10000));
            return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> System.out.println("WebFilter: Response sent with status " + exchange.getResponse().getStatusCode())));
        };
    }

    // Spring Cloud Gateway에서 사용됩니다. 즉, Gateway에 들어오는 모든 요청이 GlobalFilter를 통과
    @Component
    public class PreFilter implements GlobalFilter, Ordered {

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            // 요청 로깅
            System.out.println("Global filter Request: " + exchange.getRequest().getPath());
            //System.out.println(createToken("testUser","MASTER",10000));
            return chain.filter(exchange);
        }

        @Override
        public int getOrder() {  // 필터의 순서를 지정합니다.
            return 2;
        }
    }

}