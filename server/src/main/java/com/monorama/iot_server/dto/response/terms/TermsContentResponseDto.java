package com.monorama.iot_server.dto.response.terms;

import com.monorama.iot_server.domain.Project;
import com.monorama.iot_server.domain.type.TermsType;

public record TermsContentResponseDto(String content) {

    public static TermsContentResponseDto of(String content) {
        return new TermsContentResponseDto(content);
    }

    public static TermsContentResponseDto fromEntity(Project project, TermsType type) {
        return switch (type) {
            case PRIVACY_POLICY -> of(project.getPrivacyPolicy());
            case TERMS_OF_SERVICE -> of(project.getTermsOfPolicy());
            case HEALTH_DATA_CONSENT -> of(project.getHealthDataConsent());
            case LOCAL_DATA_TERMS_OF_SERVICE -> of(project.getLocalDataTermsOfService());
            case AIR_DATA_CONSENT -> of(project.getAirDataConsent());
        };
    }
}
