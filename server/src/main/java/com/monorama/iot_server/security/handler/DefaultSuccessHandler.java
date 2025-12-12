package com.monorama.iot_server.security.handler;

import com.monorama.iot_server.constant.Constant;
import com.monorama.iot_server.dto.JwtTokenDto;
import com.monorama.iot_server.repository.UserRepository;
import com.monorama.iot_server.security.info.UserPrincipal;
import com.monorama.iot_server.util.CookieUtil;
import com.monorama.iot_server.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONValue;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {
        // 유저의 인증된 정보를 들고오고
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        JwtTokenDto tokenDto = jwtUtil.generateTokens(userPrincipal.getId(), userPrincipal.getRole());
        userRepository.updateRefreshTokenAndLoginStatus(userPrincipal.getId(), tokenDto.getRefreshToken(), Boolean.TRUE);
        // WEB , APP 구분
        String userAgent = request.getHeader("User-Agent");

        if (userAgent == null) {
            setSuccessAppResponse(response, tokenDto);
        } else {
            setSuccessWebResponse(response, tokenDto);
        }
    }

    private void setSuccessAppResponse(HttpServletResponse response, JwtTokenDto tokenDto) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.OK.value());

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", Map.of(
                Constant.AUTHORIZATION, tokenDto.getAccessToken(),
                        Constant.REAUTHORIZATION, tokenDto.getRefreshToken()
                )
        );
        result.put("error", null);

        response.getWriter().write(JSONValue.toJSONString(result));
    }

    private void setSuccessWebResponse(HttpServletResponse response, JwtTokenDto tokenDto) throws IOException {
        CookieUtil.addCookie(response, Constant.AUTHORIZATION, tokenDto.getAccessToken());
        CookieUtil.addSecureCookie(response, Constant.REAUTHORIZATION, tokenDto.getRefreshToken(), jwtUtil.getWebRefreshTokenExpirationSecond());

        response.sendRedirect("http://localhost:5173");
    }
}