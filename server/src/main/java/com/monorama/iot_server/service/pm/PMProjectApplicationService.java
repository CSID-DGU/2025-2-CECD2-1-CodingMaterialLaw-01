package com.monorama.iot_server.service.pm;

import com.monorama.iot_server.domain.type.ProjectType;
import com.monorama.iot_server.dto.request.pm.ProjectRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PMProjectApplicationService {

    private final PMService pmService;
    private final ElasticIndexService elasticIndexService;

    public void createProjectWithIndex(Long pmId, ProjectRequestDto requestDto) {
        String indexIdentifier = pmService.saveProject(pmId, requestDto);
        ProjectType projectType = requestDto.projectType();

        if (projectType == ProjectType.HEALTH_DATA) {
            elasticIndexService.createHealthIndex(indexIdentifier);
        } else if (projectType == ProjectType.AIR_QUALITY) {
            elasticIndexService.createAirIndex(indexIdentifier);
        } else {
            elasticIndexService.createAirIndex(indexIdentifier);
            elasticIndexService.createHealthIndex(indexIdentifier);
        }

        // TODO: create index 실패시 생성된 프로젝트도 삭제
    }

}
