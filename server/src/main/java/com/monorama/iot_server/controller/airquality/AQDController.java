package com.monorama.iot_server.controller.airquality;

import com.monorama.iot_server.annotation.UserId;
import com.monorama.iot_server.dto.ResponseDto;
import com.monorama.iot_server.dto.request.airquality.AQDRequestDto;
import com.monorama.iot_server.dto.request.airquality.AQDSyncRequestDto;
import com.monorama.iot_server.service.airquality.AQDService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/air-quality-data")
public class AQDController {

    private final AQDService airService;

    @PostMapping("/realtime")
    public ResponseDto<?> saveRealtime(@UserId Long userId,
                                       @Valid @RequestBody AQDRequestDto requestDto) {
        return ResponseDto.ok(airService.saveRealtime(userId, requestDto));
    }

    @PostMapping("/sync")
    public ResponseDto<?> saveSyncHealthData(
            @UserId Long userId,
            @RequestBody @Valid AQDSyncRequestDto request
    ) {
        airService.saveSync(userId, request);
        return ResponseDto.ok(null);
    }
}