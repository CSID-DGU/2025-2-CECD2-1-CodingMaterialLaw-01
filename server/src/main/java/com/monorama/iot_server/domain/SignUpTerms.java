package com.monorama.iot_server.domain;

import com.monorama.iot_server.domain.type.ServiceType;
import com.monorama.iot_server.domain.type.SignUpTermsType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sign_up_terms_tb")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignUpTerms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "terms_type", nullable = false)
    private SignUpTermsType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    private ServiceType serviceType;

    @Column(length = 20, nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
}
