package com.monorama.iot_server.dto.request.register;

import com.monorama.iot_server.annotation.PhoneNumber;
import com.monorama.iot_server.domain.embedded.PersonalInfoItem;
import com.monorama.iot_server.domain.type.BloodType;
import com.monorama.iot_server.domain.type.Gender;
import com.monorama.iot_server.domain.type.NationalCode;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UserRegisterDto(
        @NotBlank()
        String name,

        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @NotBlank()
        String email,

        @NotNull()
        Gender gender,

        @NotNull()
        NationalCode nationalCode,

        @PhoneNumber
        String phoneNumber,

        @NotNull()
        LocalDate dateOfBirth,

        @NotNull()
        BloodType bloodType,

        @NotNull()
        @Positive()
        Double height,

        @NotNull()
        @Positive()
        Double weight
) {
    public PersonalInfoItem toEntity() {
        return new PersonalInfoItem(
                name,
                email,
                gender,
                nationalCode,
                phoneNumber,
                dateOfBirth,
                bloodType,
                height,
                weight
        );
    }
}
