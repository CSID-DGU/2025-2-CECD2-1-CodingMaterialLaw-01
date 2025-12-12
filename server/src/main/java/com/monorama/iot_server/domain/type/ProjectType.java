package com.monorama.iot_server.domain.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ProjectType {
    HEALTH_DATA("HEALTH_DATA"), AIR_QUALITY("AIR_QUALITY"), BOTH("BOTH");

    private final String projectType;

    @Override
    public String toString() {
        return projectType;
    }
}
