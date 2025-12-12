package com.monorama.iot_server.dto.response.terms;

import com.monorama.iot_server.domain.SignUpTerms;

public record SignUpTermsResponseDto(
        String type,
        String title,
        String content
) {

    public static SignUpTermsResponseDto fromEntity(SignUpTerms signUpTerms) {
        return new SignUpTermsResponseDto(
                signUpTerms.getType().toString(),
                signUpTerms.getTitle(),
                signUpTerms.getContent()
        );
    }
}
