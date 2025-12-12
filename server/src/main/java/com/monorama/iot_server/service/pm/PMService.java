package com.monorama.iot_server.service.pm;

import com.monorama.iot_server.domain.Project;
import com.monorama.iot_server.domain.User;
import com.monorama.iot_server.dto.request.pm.ProjectRequestDto;
import com.monorama.iot_server.dto.response.project.AirMetaDataItemResponseDto;
import com.monorama.iot_server.dto.response.project.ProjectDetailResponseDto;
import com.monorama.iot_server.dto.response.project.ProjectListForPMResponseDto;
import com.monorama.iot_server.dto.response.project.ProjectSimpleForPMResponseDto;
import com.monorama.iot_server.exception.CommonException;
import com.monorama.iot_server.exception.ErrorCode;
import com.monorama.iot_server.repository.AirMetaDataItemRepository;
import com.monorama.iot_server.repository.ProjectRepository;
import com.monorama.iot_server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PMService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final AirMetaDataItemRepository airMetaDataItemRepository;

    public ProjectListForPMResponseDto getProjectList(Long pmId) {
        List<ProjectSimpleForPMResponseDto> projects = projectRepository.findAllByPMId(pmId)
                .stream()
                .map(ProjectSimpleForPMResponseDto::fromEntity)
                .toList();
        return ProjectListForPMResponseDto.of(projects);
    }

    public ProjectDetailResponseDto getProjectDetail(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_PROJECT));

        List<AirMetaDataItemResponseDto> airMetaDataItemDtoList = airMetaDataItemRepository.findAllByProjectId(project.getId())
                .stream()
                .map(AirMetaDataItemResponseDto::fromEntity)
                .toList();

        return ProjectDetailResponseDto.fromEntity(project, airMetaDataItemDtoList);
    }

    public String saveProject(Long pmId, ProjectRequestDto projectRequestDto) {
        User pm = userRepository.findById(pmId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        Project project = projectRequestDto.toEntity(pm);
        projectRepository.save(project);

        return "-" + pmId + "-" + project.getId() + "-" + project.getEndDate();
    }

// TODO: 기간지난 프로젝트 보여주지 않기
}
