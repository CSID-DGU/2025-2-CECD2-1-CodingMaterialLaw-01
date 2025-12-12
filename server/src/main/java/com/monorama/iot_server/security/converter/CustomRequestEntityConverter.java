package com.monorama.iot_server.security.converter;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.DefaultOAuth2TokenRequestHeadersConverter;
import org.springframework.security.oauth2.client.endpoint.DefaultOAuth2TokenRequestParametersConverter;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;

import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

import java.security.PrivateKey;

@Getter
@Component
public class CustomRequestEntityConverter implements Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> {

    private final DefaultOAuth2TokenRequestParametersConverter<OAuth2AuthorizationCodeGrantRequest> defaultConverter = new DefaultOAuth2TokenRequestParametersConverter<>();

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

    @Override
    public RequestEntity<?> convert(OAuth2AuthorizationCodeGrantRequest oauth2AuthorizationCodeGrantRequest) {

        String registrationId = oauth2AuthorizationCodeGrantRequest.getClientRegistration().getRegistrationId();
        MultiValueMap<String, String> parameters = defaultConverter.convert(oauth2AuthorizationCodeGrantRequest);

        if(registrationId.contains("apple")) {
            parameters.set("client_secret", generateClientSecret());
        }

        URI tokenUrl = UriComponentsBuilder
                .fromUriString(oauth2AuthorizationCodeGrantRequest.getClientRegistration().getProviderDetails().getTokenUri())
                .build()
                .toUri();
        DefaultOAuth2TokenRequestHeadersConverter<OAuth2AuthorizationCodeGrantRequest> headersConverter = new DefaultOAuth2TokenRequestHeadersConverter<>();
        HttpHeaders headers = headersConverter.convert(oauth2AuthorizationCodeGrantRequest);
        return new RequestEntity<>(parameters, headers, HttpMethod.POST, tokenUrl);
    }

    public String generateClientSecret() {

        Resource privateKeyResource = new ClassPathResource(privateKeyPath.replace("classpath:", ""));

        Date expirationDate = Date.from(LocalDateTime.now().plusMinutes(2)
                .atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .setHeaderParam("alg", SignatureAlgorithm.ES256)
                .setHeaderParam("kid", keyId)
                .setIssuer(teamId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expirationDate)
                .setAudience(audience)
                .setSubject(clientId)
                .signWith(getPrivateKey(privateKeyResource), SignatureAlgorithm.ES256)
                .compact();
    }

    public PrivateKey getPrivateKey(Resource privateKeyResource) {
        try (InputStream is = privateKeyResource.getInputStream()) {
            String privateKeyContent = new String(is.readAllBytes(), StandardCharsets.UTF_8)
                    .replaceAll("-----BEGIN PRIVATE KEY-----", "")
                    .replaceAll("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] keyBytes = Base64.getDecoder().decode(privateKeyContent);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");

            return keyFactory.generatePrivate(keySpec);
        }
        catch (Exception e) {
            throw new RuntimeException("Error generating client secret", e);
        }
    }
}