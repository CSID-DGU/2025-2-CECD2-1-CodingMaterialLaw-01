package com.monorama.iot_server.domain.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TermsType {

    PRIVACY_POLICY("PRIVACY_POLICY"),
    TERMS_OF_SERVICE("TERMS_OF_SERVICE"),
    HEALTH_DATA_CONSENT("HEALTH_DATA_CONSENT"),
    LOCAL_DATA_TERMS_OF_SERVICE("LOCAL_DATA_TERMS_OF_SERVICE"),
    AIR_DATA_CONSENT("AIR_DATA_CONSENT");

    private final String value;

    @Override
    public String toString() {
        return value;
    }
}
