package com.monorama.iot_server.security.apple;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

import java.security.PrivateKey;

@Component
public class CustomClientSecretGenerator {

    @Value("${spring.security.oauth2.client.registration.apple.client-id}")
    private String clientId;
    @Value("${apple.key-id}")
    private String keyId;
    @Value("${apple.team-id}")
    private String teamId;
    @Value("${apple.audience}")
    private String audience;
    @Value("${apple.private-key}")
    private String privateKeyPath;

    public String generateClientSecret() {
        try {
            Resource privateKeyResource = new ClassPathResource(privateKeyPath.replace("classpath:", ""));
            Date expirationDate = Date.from(LocalDateTime.now().plusMinutes(2)
                    .atZone(ZoneId.systemDefault()).toInstant());

            return Jwts.builder()
                    .setHeaderParam("alg", SignatureAlgorithm.ES256)
                    .setHeaderParam("kid", keyId)
                    .setIssuer(teamId)
                    .setIssuedAt(new Date())
                    .setExpiration(expirationDate)
                    .setAudience(audience)
                    .setSubject(clientId)
                    .signWith(getPrivateKey(privateKeyResource), SignatureAlgorithm.ES256)
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate client secret", e);
        }
    }

    private PrivateKey getPrivateKey(Resource privateKeyResource) throws Exception {
        String privateKeyContent = new String(privateKeyResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyContent);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("EC").generatePrivate(keySpec);
    }
}