package com.monorama.iot_server.controller.healthdata;

import com.monorama.iot_server.annotation.UserId;
import com.monorama.iot_server.dto.ResponseDto;
import com.monorama.iot_server.dto.request.healthdata.HDRequestDto;
import com.monorama.iot_server.dto.request.healthdata.HDSyncRequestDto;
import com.monorama.iot_server.service.healthdata.HDService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/health-data")
@RequiredArgsConstructor
public class HDController {

    private final HDService service;

    @PostMapping("/realtime")
    public ResponseDto<?> saveRealtimeHealthData(
            @UserId Long userId,
            @RequestBody @Valid HDRequestDto request
    ) {
        return ResponseDto.ok(service.saveRealtime(userId, request));
    }

    @PostMapping("/sync")
    public ResponseDto<?> saveSyncHealthData(
            @UserId Long userId,
            @RequestBody @Valid HDSyncRequestDto request
    ) {
        service.saveSync(userId, request);
        return ResponseDto.ok(null);
    }
}
