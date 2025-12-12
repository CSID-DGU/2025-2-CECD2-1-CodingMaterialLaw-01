package com.monorama.iot_server.dto.request.airquality;

import java.util.List;

public record AQDSyncRequestDto(
        List<AQDRequestDto> airQualityDataList
) {}