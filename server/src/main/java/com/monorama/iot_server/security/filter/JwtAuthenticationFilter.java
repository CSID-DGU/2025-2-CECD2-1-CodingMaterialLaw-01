package com.monorama.iot_server.security.filter;

import com.monorama.iot_server.constant.Constant;
import com.monorama.iot_server.exception.CommonException;
import com.monorama.iot_server.exception.ErrorCode;
import com.monorama.iot_server.security.JwtAuthenticationProvider;
import com.monorama.iot_server.security.JwtAuthenticationToken;
import com.monorama.iot_server.security.info.JwtUserInfo;
import com.monorama.iot_server.type.ERole;
import com.monorama.iot_server.util.HeaderUtil;
import com.monorama.iot_server.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws JwtException, ServletException, IOException {

        log.info("JwtAuthenticationFilter doFilterInternal");
        log.info("request.getRequestURI() : {}", request.getRequestURI());
        log.info("request.getHeader(Authorization) : {}", request.getHeader(Constant.AUTHORIZATION_HEADER));

        final String token = HeaderUtil.refineHeader(request,Constant.AUTHORIZATION_HEADER,Constant.BEARER_PREFIX)
                .orElseThrow(() -> new CommonException(ErrorCode.INVALID_TOKEN_ERROR));

        final Claims claims = jwtUtil.validateToken(token);
        JwtUserInfo userInfo = new JwtUserInfo(
                Long.valueOf(claims.get(Constant.USER_ID_CLAIM_NAME,String.class)),
                ERole.valueOf(claims.get(Constant.USER_ROLE_CLAIM_NAME, String.class))
        );

        JwtAuthenticationToken beforeAuthentication = new JwtAuthenticationToken(
                null,
                userInfo.id(),
                userInfo.role()
        );
        UsernamePasswordAuthenticationToken afterAuthentication =
                (UsernamePasswordAuthenticationToken)  jwtAuthenticationProvider.authenticate(beforeAuthentication);

        afterAuthentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        final SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(afterAuthentication);
        SecurityContextHolder.setContext(securityContext);

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        log.info(request.getRequestURI());
        log.info(String.valueOf(Constant.NO_NEED_AUTH_URLS.contains(request.getRequestURI())));
        return Constant.NO_NEED_AUTH_URLS.contains(request.getRequestURI())
                || request.getRequestURI().startsWith("/guest");
    }

}
