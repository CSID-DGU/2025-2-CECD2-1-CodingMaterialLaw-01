package com.monorama.iot_server.dto.request.pm;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.monorama.iot_server.domain.AirMetaDataItem;
import com.monorama.iot_server.domain.Project;
import com.monorama.iot_server.domain.User;
import com.monorama.iot_server.domain.embedded.*;
import com.monorama.iot_server.domain.type.ProjectType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public record ProjectRequestDto(
        @NotNull String projectTitle,
        @NotNull Integer participant,
        @NotNull String description,
        @NotNull ProjectType projectType,

        String termsOfPolicy,
        String privacyPolicy,
        String healthDataConsent,
        String airDataConsent,
        String localDataTermsOfService,

        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING, timezone = "Asia/Seoul")
        LocalDate startDate,

        @NotNull
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        LocalDate endDate,

        @NotNull Boolean email,
        @NotNull Boolean gender,
        @NotNull Boolean nationalCode,
        @NotNull Boolean phoneNumber,
        @NotNull Boolean dateOfBirth,
        @NotNull Boolean bloodType,
        @NotNull Boolean weight,
        @NotNull Boolean height,
        @NotNull Boolean name,

        @NotNull Boolean stepCount,
        @NotNull Boolean runningSpeed,
        @NotNull Boolean basalEnergyBurned,
        @NotNull Boolean activeEnergyBurned,
        @NotNull Boolean sleepAnalysis,
        @NotNull Boolean heartRate,
        @NotNull Boolean oxygenSaturation,
        @NotNull Boolean bloodPressureSystolic,
        @NotNull Boolean bloodPressureDiastolic,
        @NotNull Boolean respiratoryRate,
        @NotNull Boolean bodyTemperature,
        @NotNull Boolean ecgData,
        @NotNull Boolean watchDeviceLatitude,
        @NotNull Boolean watchDeviceLongitude,

        @NotNull Boolean pm25Value,
        @NotNull Boolean pm25Level,
        @NotNull Boolean pm10Value,
        @NotNull Boolean pm10Level,
        @NotNull Boolean temperature,
        @NotNull Boolean temperatureLevel,
        @NotNull Boolean humidity,
        @NotNull Boolean humidityLevel,
        @NotNull Boolean co2Value,
        @NotNull Boolean co2Level,
        @NotNull Boolean vocValue,
        @NotNull Boolean vocLevel,
        @NotNull Boolean picoDeviceLatitude,
        @NotNull Boolean picoDeviceLongitude,

        List<AirMetaDataItemRequestDto> airMetaDataItemList
) {
        public Project toEntity(User user) {

                PersonalInfoFlag personalInfoFlag = PersonalInfoFlag.builder()
                        .name(name)
                        .email(email)
                        .gender(gender)
                        .nationalCode(nationalCode)
                        .phoneNumber(phoneNumber)
                        .dateOfBirth(dateOfBirth)
                        .bloodType(bloodType)
                        .height(height)
                        .weight(weight)
                        .build();

                HealthDataFlag healthDataFlag = HealthDataFlag.builder()
                        .stepCount(stepCount)
                        .runningSpeed(runningSpeed)
                        .basalEnergyBurned(basalEnergyBurned)
                        .activeEnergyBurned(activeEnergyBurned)
                        .sleepAnalysis(sleepAnalysis)
                        .heartRate(heartRate)
                        .oxygenSaturation(oxygenSaturation)
                        .bloodPressureSystolic(bloodPressureSystolic)
                        .bloodPressureDiastolic(bloodPressureDiastolic)
                        .respiratoryRate(respiratoryRate)
                        .bodyTemperature(bodyTemperature)
                        .ecgData(ecgData)
                        .watchDeviceLatitude(watchDeviceLatitude)
                        .watchDeviceLongitude(watchDeviceLongitude)
                        .build();

                AirQualityDataFlag airQualityDataFlag = AirQualityDataFlag.builder()
                        .pm25Value(pm25Value)
                        .pm25Level(pm25Level)
                        .pm10Value(pm10Value)
                        .pm10Level(pm10Level)
                        .temperature(temperature)
                        .temperatureLevel(temperatureLevel)
                        .humidity(humidity)
                        .humidityLevel(humidityLevel)
                        .co2Value(co2Value)
                        .co2Level(co2Level)
                        .vocValue(vocValue)
                        .vocLevel(vocLevel)
                        .picoDeviceLatitude(picoDeviceLatitude)
                        .picoDeviceLongitude(picoDeviceLongitude)
                        .build();

                Project project = Project.builder()
                        .projectType(projectType)
                        .title(projectTitle)
                        .participant(participant)
                        .startDate(startDate)
                        .endDate(endDate)
                        .description(description)
                        .termsOfPolicy(termsOfPolicy)
                        .privacyPolicy(privacyPolicy)
                        .healthDataConsent(healthDataConsent)
                        .airDataConsent(airDataConsent)
                        .localDataTermsOfService(localDataTermsOfService)
                        .personalInfoFlag(personalInfoFlag)
                        .healthDataFlag(healthDataFlag)
                        .airQualityDataFlag(airQualityDataFlag)
                        .build();

                project.setUser(user);

                Optional.ofNullable(airMetaDataItemList()).ifPresent(list -> list.forEach(airMetaDataItem -> {
                        AirMetaDataItem item = AirMetaDataItem.builder()
                                .dataName(airMetaDataItem.dataName())
                                .dataType(airMetaDataItem.dataType())
                                .build();
                        item.setProject(project);
                }));

                return project;
        }
}
