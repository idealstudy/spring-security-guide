package com.idealstudy.eureka.client.auth.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.idealstudy.eureka.client.auth.dto.UserDto;
import com.idealstudy.eureka.client.auth.dto.UserInfoResponseDto;
import com.idealstudy.eureka.client.auth.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = SPRING)
public interface UserMapper {

    // entity to Dto
    UserInfoResponseDto userToUserInfoResponseDto(User user);

    // entity to Dto
    UserDto userToUserDto(User user);

    // Dto to entity
    User userDtoToUser(UserDto userDto);
}