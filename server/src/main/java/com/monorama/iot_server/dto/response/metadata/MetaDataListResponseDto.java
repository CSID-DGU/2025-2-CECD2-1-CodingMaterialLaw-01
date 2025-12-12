package com.monorama.iot_server.dto.response.metadata;

import java.util.List;

public record MetaDataListResponseDto(
        List<MetaDataResponseDto> metaDataList
) {
    public static MetaDataListResponseDto of(List<MetaDataResponseDto> metaDataList) {
        return new MetaDataListResponseDto(metaDataList);
    }
}
