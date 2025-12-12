package com.monorama.iot_server.dto.response.project;

import com.monorama.iot_server.domain.Project;

public record ProjectSimpleResponseDto(
        Long projectId,
        String projectTitle,
        String termsOfPolicy,
        String privacyPolicy,
        String healthDataConsent,
        String airDataConsent,
        String localDataTermsOfService
) {
    public static ProjectSimpleResponseDto fromEntity(Project project) {
        return new ProjectSimpleResponseDto(project.getId(), project.getTitle(),
                project.getTermsOfPolicy(), project.getPrivacyPolicy(),
                project.getHealthDataConsent(), project.getAirDataConsent(),
                project.getLocalDataTermsOfService());
    }
}