package com.monorama.iot_server.dto.request.register;

import com.monorama.iot_server.annotation.PhoneNumber;
import com.monorama.iot_server.domain.embedded.PersonalInfoItem;
import com.monorama.iot_server.domain.type.Gender;
import com.monorama.iot_server.domain.type.NationalCode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PMRegisterDto(

        @NotBlank
        String name,

        @NotNull
        Gender gender,

        @NotBlank
        @PhoneNumber
        String phoneNumber,

        @NotNull
        NationalCode nationalCode,

        @NotNull
        LocalDate dateOfBirth,

        @NotBlank
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email

) {
    public PersonalInfoItem toEntity() {
        return new PersonalInfoItem(
                name,
                email,
                gender,
                nationalCode,
                phoneNumber,
                dateOfBirth,
                null, // bloodType
                null, // height
                null  // weight
        );
    }
}
