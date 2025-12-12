package com.monorama.iot_server.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Optional;

public class CookieUtil {
    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(name))
                .findFirst();
    }
    public static void addCookie(HttpServletResponse response, String name, String value){
        Cookie cookie = new Cookie(name,value);
        // 전역 사용
        cookie.setPath("/");

        response.addCookie(cookie);
    }

    public static void addSecureCookie(HttpServletResponse response,String name, String value, Integer maxAge){
//        Cookie cookie = new Cookie(name,value);
//        cookie.setPath("/");
//        cookie.setHttpOnly(true);
//        cookie.setMaxAge(maxAge);
//        cookie.setSecure(true);
//        response.addCookie(cookie);
        response.setHeader("Set-Cookie", String.format(
                "%s=%s; Path=/; Max-Age=%d; HttpOnly; Secure; SameSite=None",
                name, value, maxAge
        ));
    }
}
