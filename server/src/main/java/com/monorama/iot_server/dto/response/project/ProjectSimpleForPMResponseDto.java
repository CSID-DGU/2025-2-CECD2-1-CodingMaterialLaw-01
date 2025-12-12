package com.monorama.iot_server.dto.response.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.monorama.iot_server.domain.Project;

import java.time.LocalDate;

public record ProjectSimpleForPMResponseDto(
        Long projectId,
        String projectTitle,
        String projectType,
        Integer participant,

        @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = "Asia/Seoul")
        LocalDate startDate,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        LocalDate endDate
) {
    public static ProjectSimpleForPMResponseDto fromEntity(Project project) {
        return new ProjectSimpleForPMResponseDto(
                project.getId(),
                project.getTitle(),
                project.getProjectType().toString(),
                project.getMaxParticipant(),
                project.getStartDate(),
                project.getEndDate()
        );
    }
}
