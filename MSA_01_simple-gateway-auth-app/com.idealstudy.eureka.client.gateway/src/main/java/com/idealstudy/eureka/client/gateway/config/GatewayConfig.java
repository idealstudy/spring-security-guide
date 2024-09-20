package com.idealstudy.eureka.client.gateway.config;

import com.idealstudy.eureka.client.gateway.filter.JwtAuthGatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 라우팅시 authFilter 를 거치게 해서 JWT 를 검증하고 유저정보를 라우팅할 서비스에 넘긴다
 */
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder, JwtAuthGatewayFilter authFilter) {

        System.out.println("gateway config 실행");

        return builder.routes()
            // auth (authFilter x)
            .route("auth-service", r -> r.path("/api/auth/**")
                .uri("lb://auth-service")
            )
            // order
            .route("order-service", r -> r.path("/api/order/**")
                .filters(f -> f.filter(authFilter))
                .uri("lb://order-service")
            )
            // product
            .route("product-service", r -> r.path("/api/product/**")
                .filters(f -> f.filter(authFilter))
                .uri("lb://product-service")
            )
            .build();
    }
}