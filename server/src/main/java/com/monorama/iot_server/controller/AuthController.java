package com.monorama.iot_server.controller;

import com.monorama.iot_server.annotation.UserId;
import com.monorama.iot_server.constant.Constant;
import com.monorama.iot_server.dto.JwtTokenDto;
import com.monorama.iot_server.dto.ResponseDto;
import com.monorama.iot_server.dto.request.auth.AppleLoginRequestDto;
import com.monorama.iot_server.dto.request.auth.GoogleLoginRequestDto;
import com.monorama.iot_server.dto.request.register.UserRegisterDto;
import com.monorama.iot_server.dto.request.register.PMRegisterDto;
import com.monorama.iot_server.exception.CommonException;
import com.monorama.iot_server.exception.ErrorCode;
import com.monorama.iot_server.service.AuthService;
import com.monorama.iot_server.util.CookieUtil;
import com.monorama.iot_server.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login/apple")
    public ResponseDto<JwtTokenDto> loginWithAppleForApp(@Valid @RequestBody AppleLoginRequestDto appleLoginRequestDto) {
        return ResponseDto.ok(authService.loginWithAppleForApp(appleLoginRequestDto));
    }

    @PostMapping("/login/google")
    public ResponseDto<JwtTokenDto> loginWithGoogleForApp(@Valid @RequestBody GoogleLoginRequestDto googleLoginRequestDto) {
        return ResponseDto.ok(authService.loginWithGoogleForApp(googleLoginRequestDto));
    }

    @PatchMapping("/register/pm")
    public ResponseDto<?> registerPM(@UserId Long userId, @RequestBody @Valid PMRegisterDto registerDto, HttpServletResponse response) {
        JwtTokenDto tokenDto = authService.registerPM(userId, registerDto);
        return handleAccessTokenAndSetCookie(tokenDto, response);
    }

    @PatchMapping("/register/air-quality-data")
    public ResponseDto<JwtTokenDto> registerAQDUser(@UserId Long userId, @RequestBody @Valid UserRegisterDto registerDto) {
        return ResponseDto.ok(authService.registerAQDUser(userId, registerDto));
    }

    @PatchMapping("/register/air-quality-data/role")
    public ResponseDto<JwtTokenDto> updateAQDToBoth(@UserId Long userId) {
        return ResponseDto.ok(authService.updateAQDUserRole(userId));
    }

    @PatchMapping("/register/health-data")
    public ResponseDto<JwtTokenDto> registerHDUser(@UserId Long userId, @RequestBody @Valid UserRegisterDto registerDto) {
        return ResponseDto.ok(authService.registerHDUser(userId, registerDto));
    }

    @PatchMapping("/register/health-data/role")
    public ResponseDto<JwtTokenDto> updateHDToBoth(@UserId Long userId) {
        return ResponseDto.ok(authService.updateHDUserRole(userId));
    }

    @PatchMapping("/refresh")
    public ResponseDto<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtil.getCookie(request, Constant.REAUTHORIZATION)
                .map(Cookie::getValue)
                .orElseThrow(() -> new CommonException(ErrorCode.TOKEN_UNKNOWN_ERROR));

        log.info("refresh token : {}", refreshToken);
        JwtTokenDto newTokens = authService.refresh(refreshToken, request);
        return handleAccessTokenAndSetCookie(newTokens, response);
    }

    @DeleteMapping("/withdraw")
    public ResponseDto<?> withdrawUser(@UserId Long userId) {
        authService.withdrawUser(userId);
        return ResponseDto.ok(null);
    }

    private ResponseDto<?> handleAccessTokenAndSetCookie(JwtTokenDto tokenDto, HttpServletResponse response) {
        CookieUtil.addSecureCookie(
                response,
                Constant.REAUTHORIZATION,
                tokenDto.getRefreshToken(),
                jwtUtil.getWebRefreshTokenExpirationSecond()
        );
        return ResponseDto.created(Map.of("accessToken", tokenDto.getAccessToken()));
    }
}
