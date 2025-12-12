package com.monorama.iot_server.security.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monorama.iot_server.domain.User;
import com.monorama.iot_server.exception.CommonException;
import com.monorama.iot_server.exception.ErrorCode;
import com.monorama.iot_server.repository.UserRepository;
import com.monorama.iot_server.security.info.OAuth2UserInfo;
import com.monorama.iot_server.security.info.OAuth2UserInfoFactory;
import com.monorama.iot_server.security.info.UserPrincipal;
import com.monorama.iot_server.type.EProvider;
import com.monorama.iot_server.type.EProviderFactory;
import com.monorama.iot_server.type.ERole;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.interfaces.RSAPublicKey;
import java.util.*;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    @Value("${apple.public-key-url}")
    private String publicKeyUrl;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        if (registrationId.contains("apple")) {

            String idToken = userRequest.getAdditionalParameters().get("id_token").toString();

            RSAPublicKey publicKey = getPublicKeyFromIdToken(idToken);

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(idToken)
                    .getBody();

            OAuth2User user = new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")), claims, "sub");
            return process(userRequest, user);
        } else {
            return process(userRequest, super.loadUser(userRequest));
        }
    }

    public OAuth2User process(OAuth2UserRequest userRequest, OAuth2User oAuth2User){
        EProvider provider = EProviderFactory.of(userRequest.getClientRegistration().getRegistrationId().toUpperCase(Locale.ROOT));
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider,oAuth2User.getAttributes());

        UserRepository.UserSecurityForm userSecurityForm = userRepository.findBySocialIdAndEProvider(userInfo.getId(),provider)
                .orElseGet(()->
                {
                    User user = userRepository.save(
                            User.builder()
                                    .role(ERole.GUEST)
                                    .socialId(userInfo.getId())
                                    .provider(provider)
                                    .build()
                    );
                    return UserRepository.UserSecurityForm.invoke(user);
                });

        return UserPrincipal.create(userSecurityForm,oAuth2User.getAttributes());
    }

    public RSAPublicKey getPublicKeyFromIdToken(String idToken) {
        try {
            String[] parts = idToken.split("\\.");
            String headerJson = new String(java.util.Base64.getUrlDecoder().decode(parts[0]));

            ObjectMapper mapper = new ObjectMapper();
            JsonNode header = mapper.readTree(headerJson);
            String kid = header.get("kid").asText();
            String alg = header.get("alg").asText();

            HttpResponse<String> response;
            try (HttpClient client = HttpClient.newHttpClient()) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(publicKeyUrl))
                        .GET()
                        .build();
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            }

            JWKSet jwkSet = JWKSet.parse(response.body());
            List<JWK> matches = jwkSet.getKeys().stream()
                    .filter(jwk -> jwk.getKeyID().equals(kid) && jwk.getAlgorithm().getName().equals(alg))
                    .toList();

            if (matches.isEmpty()) {
                throw new CommonException(ErrorCode.NO_MATCH_APPLE_PUBLIC_KEY_ERROR);
            }

            return matches.getFirst().toRSAKey().toRSAPublicKey();

        } catch (Exception e) {
            throw new CommonException(ErrorCode.FAILED_LOAD_OR_PARSE_APPLE_PUBLIC_KEY_ERROR);
        }
    }
}
