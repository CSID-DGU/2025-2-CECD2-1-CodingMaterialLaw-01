package com.monorama.iot_server.dto.response.metadata;

import com.monorama.iot_server.domain.AirMetaData;

public record MetaDataResponseDto(
        Long metaDataId,
        String dataName,
        String value
) {
    public static MetaDataResponseDto fromEntity(AirMetaData metaData) {
        return new MetaDataResponseDto(metaData.getId(), metaData.getAirMetaDataItem().getDataName(), metaData.getMetaDataValue());
    }
}
