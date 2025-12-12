package com.monorama.iot_server.dto.request.metadata;

import java.util.List;

public record MetaDataListRequestDto(
        List<MetaDataRequestDto> metaDataList
) {

}
