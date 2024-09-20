package com.idealstudy.eureka.client.auth.security;

import com.idealstudy.eureka.client.auth.dto.UserDto;
import com.idealstudy.eureka.client.auth.mapper.UserMapper;
import com.idealstudy.eureka.client.auth.model.User;
import com.idealstudy.eureka.client.auth.repository.UserRepository;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j(topic = "UserDetailsServiceImpl 실행")
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * redis 에서 캐싱하여 User 정보를 가져오거나 존재하지 않는다면 RDBMS에서 가져와 redis에 저장
     */
    @Override

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


            User user = userRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not Found " + username));

            UserDto userDTO = userMapper.userToUserDto(user);

        // UserDetailsImpl 을 반환해 authentication 객체를 SecurityContext에 저장
        return new UserDetailsImpl(userMapper.userDtoToUser(userDTO));
    }

}
