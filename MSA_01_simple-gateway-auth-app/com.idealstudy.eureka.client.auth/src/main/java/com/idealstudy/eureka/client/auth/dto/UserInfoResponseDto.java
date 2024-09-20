package com.idealstudy.eureka.client.auth.dto;

import com.idealstudy.eureka.client.auth.common.shared.UserRole;

public record UserInfoResponseDto(
    String username,
    UserRole role
){
}
