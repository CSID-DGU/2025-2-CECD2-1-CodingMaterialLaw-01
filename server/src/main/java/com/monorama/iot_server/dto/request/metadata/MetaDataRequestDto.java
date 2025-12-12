package com.monorama.iot_server.dto.request.metadata;

import com.monorama.iot_server.domain.AirMetaData;
import com.monorama.iot_server.domain.AirMetaDataItem;
import com.monorama.iot_server.domain.Project;
import com.monorama.iot_server.domain.User;
import jakarta.validation.constraints.NotNull;

public record MetaDataRequestDto(
        @NotNull Long metaDataItemId,
        String value
) {
    public AirMetaData toEntity(User user, Project project, AirMetaDataItem airMetaDataItem) {
        AirMetaData airMetaData = AirMetaData.builder()
                .metaDataValue(value)
                .build();
        airMetaData.setRelations(user, project, airMetaDataItem);
        return airMetaData;
    }
}
