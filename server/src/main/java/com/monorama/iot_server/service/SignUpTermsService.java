package com.monorama.iot_server.service;

import com.monorama.iot_server.domain.SignUpTerms;
import com.monorama.iot_server.domain.type.ServiceType;
import com.monorama.iot_server.domain.type.SignUpTermsType;
import com.monorama.iot_server.dto.response.terms.SignUpTermsListResponseDto;
import com.monorama.iot_server.dto.response.terms.SignUpTermsResponseDto;
import com.monorama.iot_server.repository.SignUpTermsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SignUpTermsService {

    private final SignUpTermsRepository signUpTermsRepository;

    public SignUpTermsListResponseDto getTermsByProjectType(ServiceType serviceType) {
        List<SignUpTerms> signUpTermsList = signUpTermsRepository.findByTypeInAndServiceType(
                List.of(SignUpTermsType.PRIVACY_POLICY, SignUpTermsType.TERMS_OF_SERVICE),
                serviceType
        );

        List<SignUpTermsResponseDto> dtoList = signUpTermsList.stream()
                .map(SignUpTermsResponseDto::fromEntity)
                .collect(Collectors.toList());

        return SignUpTermsListResponseDto.of(dtoList);
    }
}


