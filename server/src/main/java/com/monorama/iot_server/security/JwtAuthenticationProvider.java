package com.monorama.iot_server.security;

import com.monorama.iot_server.security.info.JwtUserInfo;
import com.monorama.iot_server.security.info.UserPrincipal;
import com.monorama.iot_server.security.service.CustomUserDetailService;
import com.monorama.iot_server.type.ERole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final CustomUserDetailService customUserDetailService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        UserPrincipal userPrincipal = null;
        if (authentication instanceof JwtAuthenticationToken) {
            JwtUserInfo jwtUserInfo = new JwtUserInfo((Long) authentication.getPrincipal(), (ERole) authentication.getCredentials());
            userPrincipal = (UserPrincipal) customUserDetailService.loadUserByUserId(jwtUserInfo.id());

            if (userPrincipal.getRole() != jwtUserInfo.role()) {
                throw new AuthenticationException("Invalid Role") {
                };
            }
        }

        return new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(JwtAuthenticationToken.class)
                || authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
