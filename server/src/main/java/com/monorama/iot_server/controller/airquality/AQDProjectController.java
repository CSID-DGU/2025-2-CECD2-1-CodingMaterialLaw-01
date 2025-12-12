package com.monorama.iot_server.controller.airquality;

import com.monorama.iot_server.annotation.UserId;
import com.monorama.iot_server.dto.ResponseDto;
import com.monorama.iot_server.dto.response.project.ProjectDetailResponseDto;
import com.monorama.iot_server.dto.response.project.ProjectListResponseDto;
import com.monorama.iot_server.service.airquality.AQDProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/air-quality-data")
@RequiredArgsConstructor
public class AQDProjectController {

    private final AQDProjectService aqdProjectService;

    @GetMapping("/projects")
    public ResponseDto<ProjectListResponseDto> getAllProjects(@UserId Long userId) {
        return ResponseDto.ok(aqdProjectService.getAvailableAQDList(userId));
    }

    @GetMapping("/projects/{projectId}")
    public ResponseDto<ProjectDetailResponseDto> getProjectDetail(@PathVariable Long projectId) {
        return ResponseDto.ok(aqdProjectService.getAQDProjectDetail(projectId));
    }

    @PostMapping("/projects/{projectId}/participation")
    public ResponseDto<?> participateProject(@UserId Long userId,
                                             @PathVariable Long projectId) {
        aqdProjectService.participateProject(userId, projectId);
        return ResponseDto.ok(null);
    }

}
