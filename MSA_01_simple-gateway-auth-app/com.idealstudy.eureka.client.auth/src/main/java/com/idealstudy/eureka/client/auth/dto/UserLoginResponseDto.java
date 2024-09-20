package com.idealstudy.eureka.client.auth.dto;

public record UserLoginResponseDto(
    String username,
    String token
){

    public static UserLoginResponseDto createLoginReponse (String username, String token){
        return new UserLoginResponseDto(username, token);
    }
}
