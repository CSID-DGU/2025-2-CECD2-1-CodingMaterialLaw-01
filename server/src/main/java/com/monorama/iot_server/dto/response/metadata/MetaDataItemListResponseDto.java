package com.monorama.iot_server.dto.response.metadata;

import java.util.List;

public record MetaDataItemListResponseDto(
        List<MetaDataItemResponseDto> metaDataItemList
) {
    public static MetaDataItemListResponseDto of(List<MetaDataItemResponseDto> metaDataItemList) {
        return new MetaDataItemListResponseDto(metaDataItemList);
    }
}
