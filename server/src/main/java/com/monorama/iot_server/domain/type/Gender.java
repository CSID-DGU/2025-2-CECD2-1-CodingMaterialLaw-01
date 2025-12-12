package com.monorama.iot_server.domain.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Gender {
    MALE("MALE"), FEMALE("FEMALE"), OTHER("OTHER");

    private final String gender;

    @Override
    public String toString() {
        return gender;
    }

}
