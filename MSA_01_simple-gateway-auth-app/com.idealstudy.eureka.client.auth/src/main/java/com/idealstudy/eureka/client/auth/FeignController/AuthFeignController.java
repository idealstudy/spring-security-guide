package com.idealstudy.eureka.client.auth.FeignController;

import com.idealstudy.eureka.client.auth.common.exception.GlobalException;
import com.idealstudy.eureka.client.auth.common.response.CommonResponse;
import com.idealstudy.eureka.client.auth.common.response.ResultCase;
import com.idealstudy.eureka.client.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authFeign")
@RequiredArgsConstructor
@Slf4j(topic = "AuthFeignController")
public class AuthFeignController {

    private final AuthService authService;

    /**
     * 실제 유저의 아이디인지 체크
     */
    @GetMapping("/{userId}")
    public CommonResponse<String> checkUser(@RequestParam String userId) {
        if (!authService.checkUserExists(userId)) {
            throw new GlobalException(ResultCase.USER_NOT_FOUND);
        }

        return CommonResponse.success("존재하는 유저입니다");
    }
}
