package com.monorama.iot_server.domain.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class PersonalInfoFlag {

    @Column(name = "name_flag", nullable = false)
    private Boolean name = false;

    @Column(name = "email_flag", nullable = false)
    private Boolean email = false;

    @Column(name = "gender_flag", nullable = false)
    private Boolean gender = false;

    @Column(name = "national_code_flag", nullable = false)
    private Boolean nationalCode = false;

    @Column(name = "phone_number_flag", nullable = false)
    private Boolean phoneNumber = false;

    @Column(name = "date_of_birth_flag", nullable = false)
    private Boolean dateOfBirth = false;

    @Column(name = "blood_type_flag", nullable = false)
    private Boolean bloodType = false;

    @Column(name = "height_flag", nullable = false)
    private Boolean height = false;

    @Column(name = "weight_flag", nullable = false)
    private Boolean weight = false;

    @Builder
    public PersonalInfoFlag(Boolean name, Boolean email, Boolean gender, Boolean nationalCode, Boolean phoneNumber, Boolean dateOfBirth, Boolean bloodType, Boolean height, Boolean weight) {
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.nationalCode = nationalCode;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.bloodType = bloodType;
        this.height = height;
        this.weight = weight;
    }

    public void updateBy(PersonalInfoFlag projectFlag) {
        if (projectFlag.name) this.name = true;
        if (projectFlag.email) this.email = true;
        if (projectFlag.gender) this.gender = true;
        if (projectFlag.nationalCode) this.nationalCode = true;
        if (projectFlag.phoneNumber) this.phoneNumber = true;
        if (projectFlag.dateOfBirth) this.dateOfBirth = true;
        if (projectFlag.bloodType) this.bloodType = true;
        if (projectFlag.height) this.height = true;
        if (projectFlag.weight) this.weight = true;
    }
}
