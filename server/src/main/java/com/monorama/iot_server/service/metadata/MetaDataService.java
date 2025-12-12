package com.monorama.iot_server.service.metadata;

import com.monorama.iot_server.domain.AirMetaDataItem;
import com.monorama.iot_server.domain.Project;
import com.monorama.iot_server.domain.User;
import com.monorama.iot_server.domain.type.ProjectType;
import com.monorama.iot_server.dto.request.metadata.MetaDataListRequestDto;
import com.monorama.iot_server.dto.response.metadata.MetaDataItemListResponseDto;
import com.monorama.iot_server.dto.response.metadata.MetaDataItemResponseDto;
import com.monorama.iot_server.dto.response.metadata.MetaDataListResponseDto;
import com.monorama.iot_server.dto.response.metadata.MetaDataResponseDto;
import com.monorama.iot_server.exception.CommonException;
import com.monorama.iot_server.exception.ErrorCode;
import com.monorama.iot_server.repository.AirMetaDataItemRepository;
import com.monorama.iot_server.repository.AirMetaDataRepository;
import com.monorama.iot_server.repository.ProjectRepository;
import com.monorama.iot_server.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MetaDataService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final AirMetaDataRepository airMetaDataRepository;
    private final AirMetaDataItemRepository airMetaDataItemRepository;

    public MetaDataItemListResponseDto getMetaDataItemList(Long projectId) {
        List<MetaDataItemResponseDto> metaDataItemList = airMetaDataItemRepository.findAllByProjectId(projectId)
                .stream()
                .map(MetaDataItemResponseDto::fromEntity)
                .toList();
        return MetaDataItemListResponseDto.of(metaDataItemList);
    }

    public void saveMetaData(Long userId, Long projectId, MetaDataListRequestDto metaDataList) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        Project project = projectRepository.findByIdAndProjectTypeNot(projectId, ProjectType.HEALTH_DATA)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_PROJECT));

        if (airMetaDataRepository.existsByUserIdAndProjectId(userId, projectId)) {
            throw new CommonException(ErrorCode.ALREADY_EXISTS_METADATA);
        }

        airMetaDataRepository.saveAll(
                metaDataList.metaDataList().stream()
                        .map(dto -> {
                            AirMetaDataItem item = airMetaDataItemRepository.findById(dto.metaDataItemId())
                                    .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_METADATA_ITEM));
                            return dto.toEntity(user, project, item);
                        })
                        .toList()
        );
    }

    public MetaDataListResponseDto getMetaDataList(Long projectId) {
        List<MetaDataResponseDto> metaDataList = airMetaDataRepository.findAllByProjectId(projectId)
                .stream()
                .map(MetaDataResponseDto::fromEntity)
                .toList();
        return MetaDataListResponseDto.of(metaDataList);
    }
}
