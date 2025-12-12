package com.monorama.iot_server.domain.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum NationalCode {
    US("+1"),
    KR("+82");

    private final String nationalCode;

    @Override
    public String toString() {
        return nationalCode;
    }

}
