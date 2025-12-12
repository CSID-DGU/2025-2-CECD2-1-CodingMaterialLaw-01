package com.monorama.iot_server.dto.response.project;

import java.util.List;

public record ProjectListResponseDto(
        List<ProjectSimpleResponseDto> projectList
) {
    public static ProjectListResponseDto of(List<ProjectSimpleResponseDto> projects) {
        return new ProjectListResponseDto(projects);
    }
}

