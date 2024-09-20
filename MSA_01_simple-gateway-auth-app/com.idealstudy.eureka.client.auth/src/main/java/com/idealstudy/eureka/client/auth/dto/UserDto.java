package com.idealstudy.eureka.client.auth.dto;
// LocalDateTime 을 제거한 redis 캐싱용 Dto

import com.idealstudy.eureka.client.auth.common.shared.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {

    private String username;
    private String password;
    private UserRole role;
}
