package com.monorama.iot_server.dto.request.healthdata;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.monorama.iot_server.domain.HealthData;
import com.monorama.iot_server.domain.User;
import com.monorama.iot_server.domain.embedded.HealthDataFlag;
import com.monorama.iot_server.domain.embedded.HealthDataItem;
import com.monorama.iot_server.exception.CommonException;
import com.monorama.iot_server.exception.ErrorCode;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record HDRequestDto(
        @NotNull
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime createdAt,

        Double stepCount,
        Double runningSpeed,
        Double basalEnergyBurned,
        Double activeEnergyBurned,
        String sleepAnalysis,
        Double heartRate,
        Double oxygenSaturation,
        Double bloodPressureSystolic,
        Double bloodPressureDiastolic,
        Double respiratoryRate,
        Double bodyTemperature,
        String ecgData,
        Double watchDeviceLatitude,
        Double watchDeviceLongitude
) {
    public HealthData toEntity(User user) {
        HealthDataItem item = HealthDataItem.builder()
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

        return HealthData.builder()
                .createAt(createdAt)
                .user(user)
                .healthDataItem(item)
                .build();
    }

    public List<String> getMissingFields(User user) {
        if (user.getUserDataPermission() == null) {
            throw new CommonException(ErrorCode.NOT_FOUND_RESOURCE);
        }

        List<String> missingFields = new ArrayList<>();
        HealthDataFlag flag = user.getUserDataPermission().getHealthDataFlag();

        if (Boolean.TRUE.equals(flag.getStepCount()) && stepCount == null) missingFields.add("stepCount");
        if (Boolean.TRUE.equals(flag.getRunningSpeed()) && runningSpeed == null) missingFields.add("runningSpeed");
        if (Boolean.TRUE.equals(flag.getBasalEnergyBurned()) && basalEnergyBurned == null) missingFields.add("basalEnergyBurned");
        if (Boolean.TRUE.equals(flag.getActiveEnergyBurned()) && activeEnergyBurned == null) missingFields.add("activeEnergyBurned");
        if (Boolean.TRUE.equals(flag.getSleepAnalysis()) && sleepAnalysis == null) missingFields.add("sleepAnalysis");
        if (Boolean.TRUE.equals(flag.getHeartRate()) && heartRate == null) missingFields.add("heartRate");
        if (Boolean.TRUE.equals(flag.getOxygenSaturation()) && oxygenSaturation == null) missingFields.add("oxygenSaturation");
        if (Boolean.TRUE.equals(flag.getBloodPressureSystolic()) && bloodPressureSystolic == null) missingFields.add("bloodPressureSystolic");
        if (Boolean.TRUE.equals(flag.getBloodPressureDiastolic()) && bloodPressureDiastolic == null) missingFields.add("bloodPressureDiastolic");
        if (Boolean.TRUE.equals(flag.getRespiratoryRate()) && respiratoryRate == null) missingFields.add("respiratoryRate");
        if (Boolean.TRUE.equals(flag.getBodyTemperature()) && bodyTemperature == null) missingFields.add("bodyTemperature");
        if (Boolean.TRUE.equals(flag.getEcgData()) && ecgData == null) missingFields.add("ecgData");
        if (Boolean.TRUE.equals(flag.getWatchDeviceLatitude()) && watchDeviceLatitude == null) missingFields.add("watchDeviceLatitude");
        if (Boolean.TRUE.equals(flag.getWatchDeviceLongitude()) && watchDeviceLongitude == null) missingFields.add("watchDeviceLongitude");

        return missingFields;
    }

}
