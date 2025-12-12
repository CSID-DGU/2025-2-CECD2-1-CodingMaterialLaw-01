package com.monorama.iot_server.dto.response.project;

import com.monorama.iot_server.domain.AirMetaDataItem;
import com.monorama.iot_server.domain.type.DataType;

public record AirMetaDataItemResponseDto(
        String dataName,
        DataType dataType
) {
    public static AirMetaDataItemResponseDto fromEntity(AirMetaDataItem airMetaDataItem) {
        return new AirMetaDataItemResponseDto(
                airMetaDataItem.getDataName(),
                airMetaDataItem.getDataType()
        );
    }
}
