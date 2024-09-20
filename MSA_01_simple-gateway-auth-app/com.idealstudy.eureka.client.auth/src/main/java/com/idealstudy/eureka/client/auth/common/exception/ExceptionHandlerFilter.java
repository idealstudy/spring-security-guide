package com.idealstudy.eureka.client.auth.common.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.idealstudy.eureka.client.auth.common.response.CommonResponse;
import com.idealstudy.eureka.client.auth.common.response.ResultCase;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (GlobalException e) {
            setErrorResponse(response, e.getResultCase());
        }
    }

    private void setErrorResponse(HttpServletResponse response, ResultCase resultCase) {
        
        response.setStatus(resultCase.getHttpStatus().value()); // HttpStatus 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); // Content-Type : application/json
        response.setCharacterEncoding(StandardCharsets.UTF_8.name()); // charset : UTF8

        try {
            String responseJson = objectMapper.writeValueAsString(CommonResponse.error(resultCase));
            response.getWriter().write(responseJson);
        } catch (IOException e) {
            log.error("예외 필터 직렬화 오류", e);
        }
    }
}
