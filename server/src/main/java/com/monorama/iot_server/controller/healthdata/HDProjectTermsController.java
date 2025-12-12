package com.monorama.iot_server.controller.healthdata;

import com.monorama.iot_server.domain.type.TermsType;
import com.monorama.iot_server.dto.ResponseDto;
import com.monorama.iot_server.dto.response.terms.TermsContentResponseDto;
import com.monorama.iot_server.service.healthdata.HDProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/health-data/{projectId}/terms")
public class HDProjectTermsController {

    private final HDProjectService projectService;

    @GetMapping("/privacy-policy")
    public ResponseDto<TermsContentResponseDto> getPrivacyPolicy(@PathVariable Long projectId) {
        return ResponseDto.ok(projectService.getTermsContent(projectId, TermsType.PRIVACY_POLICY));
    }

    @GetMapping("/terms-of-service")
    public ResponseDto<TermsContentResponseDto> getTermsOfService(@PathVariable Long projectId) {
        return ResponseDto.ok(projectService.getTermsContent(projectId, TermsType.TERMS_OF_SERVICE));
    }

    @GetMapping("/health-data-consent")
    public ResponseDto<TermsContentResponseDto> getHealthDataConsent(@PathVariable Long projectId) {
        return ResponseDto.ok(projectService.getTermsContent(projectId, TermsType.HEALTH_DATA_CONSENT));
    }

    @GetMapping("/location-data-consent")
    public ResponseDto<TermsContentResponseDto> getLocationDataConsent(@PathVariable Long projectId) {
        return ResponseDto.ok(projectService.getTermsContent(projectId, TermsType.LOCAL_DATA_TERMS_OF_SERVICE));
    }

    @GetMapping("/air-data-consent")
    public ResponseDto<TermsContentResponseDto> getAirDataConsent(@PathVariable Long projectId) {
        return ResponseDto.ok(projectService.getTermsContent(projectId, TermsType.AIR_DATA_CONSENT));
    }
}
