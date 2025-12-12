package com.monorama.iot_server.dto.response.metadata;

import com.monorama.iot_server.domain.AirMetaDataItem;
import com.monorama.iot_server.domain.type.DataType;


public record MetaDataItemResponseDto(
    Long metaDataItemId,
    String dataName,
    DataType dataType
) {
    public static MetaDataItemResponseDto fromEntity(AirMetaDataItem metaDataItem) {
        return new MetaDataItemResponseDto(metaDataItem.getId(), metaDataItem.getDataName(), metaDataItem.getDataType());
    }
}
