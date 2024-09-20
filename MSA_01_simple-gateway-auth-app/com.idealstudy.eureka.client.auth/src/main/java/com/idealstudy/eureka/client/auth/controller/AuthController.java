package com.idealstudy.eureka.client.auth.controller;

import com.idealstudy.eureka.client.auth.common.response.CommonResponse;
import com.idealstudy.eureka.client.auth.common.response.CommonResponse.CommonEmptyRes;
import com.idealstudy.eureka.client.auth.common.shared.UserRole;
import com.idealstudy.eureka.client.auth.dto.UserInfoResponseDto;
import com.idealstudy.eureka.client.auth.dto.UserSignupRequestDto;
import com.idealstudy.eureka.client.auth.dto.UserSignupResponseDto;
import com.idealstudy.eureka.client.auth.service.AuthService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j(topic = "AuthController")
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입
     */
    @PostMapping("/sign-up")
    public CommonResponse<UserSignupResponseDto> signup(@RequestBody UserSignupRequestDto request) {
        log.info("Signup request: {}", request);

        UserSignupResponseDto username = authService.signup(request);
        return CommonResponse.success(username);
    }

    /**
     * 로그아웃
     */
    // TODO :로그아웃 (Spring Security에서 기본적으로 Stateless 설정으로 인해 로그아웃 기능은 필요하지 않지만, 추가적으로 구현할 수 있다?)
    // TODO : JWT 특성상 서버에서 제어권이 없기 때문에 로그아웃을 구현할 수 없지만, 서버에 로그아웃된 JWT를 블랙리스트로 저장해두고 이후 동일한 토큰으로 요청 시 로그인이 필요하다고 응답하는 식으로 구현하면 됩니다. 로그인 시에는 블랙리스트가 있는지 확인 후 있으면 삭제해주는 식으로요!
    @PostMapping("/logout")
    public CommonResponse<CommonEmptyRes> logout() {
        SecurityContextHolder.clearContext();
        return CommonResponse.success();
    }

    /**
     * 회원정보 단일검색
     */
    @GetMapping("/{username}")
    public CommonResponse<UserInfoResponseDto> getUser(@PathVariable String username) {
        UserInfoResponseDto userInfoResponseDto = authService.getUserByUsername(username);

        return CommonResponse.success(userInfoResponseDto);
    }

    /**
     * 회원정보 전체검색 (페이징 포함) - 관리자기능
     */
    @GetMapping("/all")
    public CommonResponse<List<UserInfoResponseDto>> getAllUsers(
        // TODO : Pageable 객체를 바로 받는 것은 어떠신가요
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "createdAt") String sortBy) {

        // TODO : log.info("메소드 getAllUsers 실행 권한 : {}", UserRole.Authority.MASTER); 로 동일하게 작성할 수 있습니다.
        //문자열끼리 + 연산자로 하면 자바에서는 문자열이 불변이므로 새로 문자열을 만들게 됩니다. 그리고 info(...) 메서드의 파라미터로 값을 넣기 위해서 + 연산으로 문자열을 계속 새로 만들게 되어 불필요한 연산을 하게 됩니다.
        //따라서 가변적으로 넣을 부분에는 {} 로 작성하시고, {} 순서대로 매개변수로 계속 넣으시면 됩니다.
        log.info("메소드 getAllUsers 실행 권한 : " + UserRole.Authority.MASTER);

        List<UserInfoResponseDto> users = authService.getAllUsers(page, size, sortBy);
        return CommonResponse.success(users);
    }


}