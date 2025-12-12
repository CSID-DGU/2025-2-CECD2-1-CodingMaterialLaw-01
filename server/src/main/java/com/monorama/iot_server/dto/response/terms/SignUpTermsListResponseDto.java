package com.monorama.iot_server.dto.response.terms;

import java.util.List;

public record SignUpTermsListResponseDto(
        List<SignUpTermsResponseDto> terms
) {
    public static SignUpTermsListResponseDto of(List<SignUpTermsResponseDto> terms) {
        return new SignUpTermsListResponseDto(terms);
    }
}
