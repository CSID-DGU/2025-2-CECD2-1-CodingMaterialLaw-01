package com.monorama.iot_server.controller.metadata;

import com.monorama.iot_server.annotation.UserId;
import com.monorama.iot_server.dto.ResponseDto;
import com.monorama.iot_server.dto.request.metadata.MetaDataListRequestDto;
import com.monorama.iot_server.dto.response.metadata.MetaDataItemListResponseDto;
import com.monorama.iot_server.dto.response.metadata.MetaDataListResponseDto;
import com.monorama.iot_server.service.metadata.MetaDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/metadata")
public class MetaDataController {
    private final MetaDataService metaDataService;

    @GetMapping("/{projectId}/item")
    public ResponseDto<MetaDataItemListResponseDto> getMetaDataItemList(@PathVariable Long projectId) {
        return ResponseDto.ok(metaDataService.getMetaDataItemList(projectId));
    }

    @PostMapping("/{projectId}")
    public ResponseDto<?> saveMetaData(@UserId Long userId, @PathVariable Long projectId, @RequestBody MetaDataListRequestDto metaDataList) {
        metaDataService.saveMetaData(userId, projectId, metaDataList);
        return ResponseDto.created(null);
    }

    @GetMapping("/{projectId}")
    public ResponseDto<MetaDataListResponseDto> getMetaDataList(@PathVariable Long projectId) {
        return ResponseDto.ok(metaDataService.getMetaDataList(projectId));
    }
}
