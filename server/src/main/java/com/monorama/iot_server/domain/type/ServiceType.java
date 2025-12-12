package com.monorama.iot_server.domain.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ServiceType {
    HEALTH_DATA("HEALTH_DATA"), AIR_QUALITY("AIR_QUALITY"), PM_WEB("PM_WEB");

    private final String serviceType;

    @Override
    public String toString() {
        return serviceType;
    }
}
