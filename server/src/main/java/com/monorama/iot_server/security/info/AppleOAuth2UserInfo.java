package com.monorama.iot_server.security.info;

import java.util.Map;

public class AppleOAuth2UserInfo extends OAuth2UserInfo {
    public AppleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        Object sub = attributes.get("sub");
        return sub != null ? sub.toString() : null;
    }
}

