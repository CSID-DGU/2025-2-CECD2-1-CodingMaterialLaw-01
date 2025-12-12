package com.monorama.iot_server.domain.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SignUpTermsType {
    PRIVACY_POLICY("PRIVACY_POLICY"),
    TERMS_OF_SERVICE("TERMS_OF_SERVICE");

    private final String termsType;

    @Override
    public String toString() {
        return termsType;
    }
}
