package com.monorama.iot_server.util;

import com.monorama.iot_server.constant.Constant;
import com.monorama.iot_server.dto.JwtTokenDto;
import com.monorama.iot_server.exception.CommonException;
import com.monorama.iot_server.exception.ErrorCode;
import com.monorama.iot_server.type.ERole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil implements InitializingBean {
    @Value("${jwt.access-expiration-ms}")
    private Integer ACCESS_EXPIRED_MS;
    @Value("${jwt.refresh-expiration-ms}")
    private Integer REFRESH_EXPIRED_MS;
    @Value("${jwt.secret}")
    private String secretKey;
    private Key key;

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

//    public JwtTokenDto generateTokens(Long id, ERole eRole) {
//        return new JwtTokenDto(generateAccessToken(id, eRole, ACCESS_EXPIRED_MS),
//                generateToken(null, REFRESH_EXPIRED_MS));
//    }

    public JwtTokenDto generateTokens(Long id, ERole eRole) {
        return new JwtTokenDto(
                generateAccessToken(id, eRole, ACCESS_EXPIRED_MS),
                generateRefreshToken(id)
        );
    }

    public Claims validateToken(final String token) throws JwtException {
        final JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public String generateToken(Claims claims, final Integer expirationPeriod) {
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationPeriod))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(Long id) {
        final Claims claims = Jwts.claims();
        claims.put(Constant.USER_ID_CLAIM_NAME, id.toString());

        return generateToken(claims, REFRESH_EXPIRED_MS);
    }

    public String generateAccessToken(final Long id, final ERole role, final Integer expirationPeriod) {
        final Claims claims = Jwts.claims();
        claims.put(Constant.USER_ID_CLAIM_NAME, id.toString());
        claims.put(Constant.USER_ROLE_CLAIM_NAME, role.toString());

        return generateToken(claims, expirationPeriod);
    }

    public Long extractUserIdFromToken(String token) {
        try {
            Claims claims = validateToken(token);
            String userIdStr = claims.get(Constant.USER_ID_CLAIM_NAME, String.class);
            return Long.valueOf(userIdStr);
        } catch (Exception e) {
            log.warn("유효하지 않은 토큰입니다: {}", e.getMessage());
            throw new CommonException(ErrorCode.INVALID_TOKEN_ERROR);
        }
    }

    public int getAccessTokenExpiration() {
        return ACCESS_EXPIRED_MS;
    }

    public int getWebRefreshTokenExpirationSecond() {
        return (REFRESH_EXPIRED_MS / 1000);
    }

}