package com.monorama.iot_server.repository;

import com.monorama.iot_server.domain.SignUpTerms;
import com.monorama.iot_server.domain.type.ServiceType;
import com.monorama.iot_server.domain.type.SignUpTermsType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SignUpTermsRepository extends JpaRepository<SignUpTerms, Long> {
    List<SignUpTerms> findByTypeInAndServiceType(List<SignUpTermsType> types, ServiceType serviceType);
}