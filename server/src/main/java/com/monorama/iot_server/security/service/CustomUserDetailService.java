package com.monorama.iot_server.security.service;

import com.monorama.iot_server.exception.CommonException;
import com.monorama.iot_server.exception.ErrorCode;
import com.monorama.iot_server.repository.UserRepository;
import com.monorama.iot_server.security.info.UserPrincipal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final UserRepository.UserSecurityForm user = userRepository.findBySocialId(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not Found Username"));

        return UserPrincipal.create(user);
    }


    public UserDetails loadUserByUserId(Long userId) throws CommonException {
        final UserRepository.UserSecurityForm user = userRepository.findByIdAndIsLoginAndRefreshTokenIsNotNull(userId,true)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        return UserPrincipal.create(user);
    }
}

