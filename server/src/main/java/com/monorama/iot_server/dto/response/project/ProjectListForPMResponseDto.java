package com.monorama.iot_server.dto.response.project;

import java.util.List;

public record ProjectListForPMResponseDto(
        List<ProjectSimpleForPMResponseDto> projectList
) {
    public static ProjectListForPMResponseDto of(List<ProjectSimpleForPMResponseDto> projects) {
        return new ProjectListForPMResponseDto(projects);
    }
}
