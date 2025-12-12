package com.monorama.iot_server.controller.airquality;

import com.monorama.iot_server.domain.type.ServiceType;
import com.monorama.iot_server.dto.ResponseDto;
import com.monorama.iot_server.dto.response.terms.SignUpTermsListResponseDto;
import com.monorama.iot_server.service.SignUpTermsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/air-quality-data/terms")
@RequiredArgsConstructor
public class AQDSignUpTermsController {

    private final SignUpTermsService signUpTermsService;

    @GetMapping
    public ResponseDto<SignUpTermsListResponseDto> getTerms() {
        SignUpTermsListResponseDto result = signUpTermsService.getTermsByProjectType(ServiceType.AIR_QUALITY);
        return ResponseDto.ok(result);
    }
}