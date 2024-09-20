package com.idealstudy.eureka.client.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final AuthFeignClient authFeignClient;

    public String checkIfUserExists(String userId) {
        // Feign을 사용하여 Auth 서비스의 checkUser 호출
        return authFeignClient.checkUser(userId);
    }
}
