package com.monorama.iot_server.dto.request.pm;

import com.monorama.iot_server.domain.type.DataType;

public record AirMetaDataItemRequestDto(
        String dataName,
        DataType dataType
) {
}
