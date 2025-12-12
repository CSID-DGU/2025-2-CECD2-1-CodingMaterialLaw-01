package com.monorama.iot_server.domain.embedded;

import com.monorama.iot_server.domain.type.BloodType;
import com.monorama.iot_server.domain.type.Gender;
import com.monorama.iot_server.domain.type.NationalCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PersonalInfoItem {

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "national_code")
    private NationalCode nationalCode;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "blood_type")
    private BloodType bloodType;

    @Column(name = "height")
    private Double height;

    @Column(name = "weight")
    private Double weight;

    public void withdraw() {
        this.name = null;
        this.email = null;
        this.phoneNumber = null;
        this.nationalCode = null;
    }
}

