package com.monorama.iot_server.domain.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DataType {
    INTEGER("INTEGER"), STRING("STRING"), BOOLEAN("BOOLEAN"), DOUBLE("DOUBLE");

    private final String dataType;

    @Override
    public String toString() {
        return dataType;
    }
}
