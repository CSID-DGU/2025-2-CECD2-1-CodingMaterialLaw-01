package com.monorama.iot_server.controller.healthdata;

import com.monorama.iot_server.domain.type.ServiceType;
import com.monorama.iot_server.dto.ResponseDto;
import com.monorama.iot_server.dto.response.terms.SignUpTermsListResponseDto;
import com.monorama.iot_server.service.SignUpTermsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/health-data/terms")
@RequiredArgsConstructor
public class HDSignUpTermsController {

    private final SignUpTermsService signUpTermsService;

    @GetMapping
    public ResponseDto<SignUpTermsListResponseDto> getTerms() {
        SignUpTermsListResponseDto result = signUpTermsService.getTermsByProjectType(ServiceType.HEALTH_DATA);
        return ResponseDto.ok(result);
    }
}