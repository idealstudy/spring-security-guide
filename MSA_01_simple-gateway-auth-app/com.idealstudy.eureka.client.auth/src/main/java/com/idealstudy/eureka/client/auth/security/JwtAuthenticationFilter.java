package com.idealstudy.eureka.client.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.idealstudy.eureka.client.auth.common.response.CommonResponse;
import com.idealstudy.eureka.client.auth.common.shared.UserRole;
import com.idealstudy.eureka.client.auth.dto.UserLoginRequestDto;
import com.idealstudy.eureka.client.auth.dto.UserLoginResponseDto;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j(topic = "JwtAuthenticationFilter - 로그인 및 JWT 생성")
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;

    @PostConstruct
    void setup() {
        setFilterProcessesUrl("/user/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
        HttpServletResponse response) throws AuthenticationException {
        log.info("로그인 시도");
        try {
            UserLoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(),
                UserLoginRequestDto.class);
            log.info(requestDto.toString());

            return getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(
                    requestDto.username(),
                    requestDto.password(),
                    null
                )
            );
            // TODO : 필터 레벨에서 동일한 양식의 에러 응답을 하려면 에러 처리 필터를 만든 후, 필터 순서를 가장 앞에 둔 다음, 직접 ObjectMapper로 json으로 직렬화 하시면 됩니다..!
            // 예전에 제가 구현한 코드 참고하시면 좋을 것 같아요 (https://github.com/yun-studio/insight-backend/blob/main/src/main/java/com/yunstudio/insight/global/exception/ExceptionHandlerFilter.java)
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain, Authentication authResult)
        throws IOException, ServletException {
        log.info("로그인 성공 및 JWT 생성");
        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
        UserRole role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole();

        String token = jwtUtil.createToken(username, role);
        jwtUtil.addJwtToCookie(token, response);

        log.info("JWT 토큰 {}",token);

        // 로그인 결과를 담은 DTO 생성
        UserLoginResponseDto dto = UserLoginResponseDto.createLoginReponse(username,token);

        // 응답을 JSON으로 직렬화하여 응답 본문에 작성
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // 응답 객체를 JSON으로 변환하여 response에 직접 씀
        ObjectMapper objectMapper = new ObjectMapper(); // Jackson ObjectMapper 사용
        response.getWriter().write(objectMapper.writeValueAsString(CommonResponse.success(dto)));

        // writer를 플러시하여 응답 전송
        response.getWriter().flush();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, AuthenticationException failed)
        throws IOException, ServletException {
        log.info("로그인 실패");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
}