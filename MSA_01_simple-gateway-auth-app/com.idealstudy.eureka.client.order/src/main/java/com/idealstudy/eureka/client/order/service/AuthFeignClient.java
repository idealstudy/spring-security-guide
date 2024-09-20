package com.idealstudy.eureka.client.order.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-service", path = "/api/authFeign")
public interface AuthFeignClient {

    @GetMapping("/checkUser")
    String checkUser(@RequestParam("userId") String userId);
}