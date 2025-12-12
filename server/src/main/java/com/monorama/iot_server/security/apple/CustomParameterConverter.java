package com.monorama.iot_server.security.apple;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.client.endpoint.DefaultOAuth2TokenRequestParametersConverter;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

@Component
@RequiredArgsConstructor
public class CustomParameterConverter implements Converter<OAuth2AuthorizationCodeGrantRequest, MultiValueMap<String, String>> {

    private final CustomClientSecretGenerator clientSecretGenerator;

    @Override
    public MultiValueMap<String, String> convert(@NonNull OAuth2AuthorizationCodeGrantRequest request) {
        final DefaultOAuth2TokenRequestParametersConverter<OAuth2AuthorizationCodeGrantRequest> defaultConverter = new DefaultOAuth2TokenRequestParametersConverter<>();
        MultiValueMap<String, String> parameters = defaultConverter.convert(request);
        if (request.getClientRegistration().getRegistrationId().equals("apple")) {
            parameters.set("client_secret", clientSecretGenerator.generateClientSecret());
        }
        return parameters;
    }

}
