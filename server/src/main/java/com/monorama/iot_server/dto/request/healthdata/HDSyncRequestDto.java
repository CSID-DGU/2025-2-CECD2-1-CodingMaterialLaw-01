package com.monorama.iot_server.dto.request.healthdata;

import java.util.List;

public record HDSyncRequestDto(
        List<HDRequestDto> healthDataList
) {}
