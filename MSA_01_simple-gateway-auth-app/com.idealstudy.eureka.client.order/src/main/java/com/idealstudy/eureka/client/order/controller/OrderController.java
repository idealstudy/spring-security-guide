package com.idealstudy.eureka.client.order.controller;

import com.idealstudy.eureka.client.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final RestTemplateBuilder restTemplateBuilder;

    @GetMapping("/checkUser")
    public ResponseEntity<String> checkUser(@RequestParam String userId) {
        System.out.println("일단 요청은 들어옴");
        String result = orderService.checkIfUserExists(userId);
        System.out.println("일단 feign 도 됨");
        if (result != null ) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(404).body("Feign Client 응답 널");
        }
    }
}
