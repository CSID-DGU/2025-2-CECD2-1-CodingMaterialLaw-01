package com.monorama.iot_server.service.healthdata;

import com.monorama.iot_server.domain.Project;
import com.monorama.iot_server.domain.User;
import com.monorama.iot_server.domain.UserDataPermission;
import com.monorama.iot_server.domain.UserProject;
import com.monorama.iot_server.domain.type.TermsType;
import com.monorama.iot_server.dto.response.project.AirMetaDataItemResponseDto;
import com.monorama.iot_server.dto.response.project.ProjectDetailResponseDto;
import com.monorama.iot_server.dto.response.project.ProjectListResponseDto;
import com.monorama.iot_server.dto.response.project.ProjectSimpleResponseDto;
import com.monorama.iot_server.dto.response.terms.TermsContentResponseDto;
import com.monorama.iot_server.exception.CommonException;
import com.monorama.iot_server.repository.AirMetaDataItemRepository;
import com.monorama.iot_server.repository.ProjectRepository;
import com.monorama.iot_server.repository.UserProjectRepository;
import com.monorama.iot_server.repository.UserRepository;
import com.monorama.iot_server.type.ERole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.monorama.iot_server.exception.ErrorCode;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HDProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final UserProjectRepository userProjectRepository;
    private final AirMetaDataItemRepository airMetaDataItemRepository;

    public ProjectListResponseDto getAvailableHDProjectList(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        List<ProjectSimpleResponseDto> projects = projectRepository.findActiveHealthProjects((user.getRole() == ERole.BOTH_USER))
                .stream()
                .map(ProjectSimpleResponseDto::fromEntity)
                .toList();

        return ProjectListResponseDto.of(projects);
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

    public TermsContentResponseDto getTermsContent(Long projectId, TermsType type) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE));

        return TermsContentResponseDto.fromEntity(project, type);
    }

    @Transactional
    public void participateProject(Long userId, Long projectId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        Project project = projectRepository.findActiveHealthProjectById(projectId)
                .orElseThrow(() -> new CommonException(ErrorCode.PROJECT_NOT_AVAILABLE));

        if (userProjectRepository.existsByUserAndProject(user, project)) {
            throw new CommonException(ErrorCode.ALREADY_JOINED_PROJECT);
        }

        project.increaseParticipant();

        userProjectRepository.save(
                UserProject.builder()
                        .user(user)
                        .project(project)
                        .build()
        );

        UserDataPermission permission = user.getUserDataPermission();
        permission.getAirQualityDataFlag().updateBy(project.getAirQualityDataFlag());
        permission.getHealthDataFlag().updateBy(project.getHealthDataFlag());
        permission.getPersonalInfoFlag().updateBy(project.getPersonalInfoFlag());
    }
}