package com.monorama.iot_server.domain.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BloodType {
    A_PLUS("A+"), A_MINUS("A-"), B_PLUS("B+"), B_MINUS("B-"),
    O_PLUS("O+"), O_MINUS("O-"), AB_PLUS("AB+"), AB_MINUS("AB-"), UNKNOWN("Unknown");

    private final String bloodType;

    @Override
    public String toString() {
        return bloodType;
    }

}
