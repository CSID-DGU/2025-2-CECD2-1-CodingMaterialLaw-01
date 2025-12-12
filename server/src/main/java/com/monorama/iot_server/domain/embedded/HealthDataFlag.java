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
public class HealthDataFlag {

    @Column(name = "step_count_flag", nullable = false)
    private Boolean stepCount = false;

    @Column(name = "running_speed_flag", nullable = false)
    private Boolean runningSpeed = false;

    @Column(name = "basal_energy_burned_flag", nullable = false)
    private Boolean basalEnergyBurned = false;

    @Column(name = "active_energy_burned_flag", nullable = false)
    private Boolean activeEnergyBurned = false;

    @Column(name = "sleep_analysis_flag", nullable = false)
    private Boolean sleepAnalysis = false;

    @Column(name = "heart_rate_flag", nullable = false)
    private Boolean heartRate = false;

    @Column(name = "oxygen_saturation_flag", nullable = false)
    private Boolean oxygenSaturation = false;

    @Column(name = "blood_pressure_systolic_flag", nullable = false)
    private Boolean bloodPressureSystolic = false;

    @Column(name = "blood_pressure_diastolic_flag", nullable = false)
    private Boolean bloodPressureDiastolic = false;

    @Column(name = "respiratory_rate_flag", nullable = false)
    private Boolean respiratoryRate = false;

    @Column(name = "body_temperature_flag", nullable = false)
    private Boolean bodyTemperature = false;

    @Column(name = "ecg_data_flag", nullable = false)
    private Boolean ecgData = false;

    @Column(name = "watch_device_latitude_flag", nullable = false)
    private Boolean watchDeviceLatitude = false;

    @Column(name = "watch_device_longitude_flag", nullable = false)
    private Boolean watchDeviceLongitude = false;

    @Builder
    public HealthDataFlag(Boolean stepCount, Boolean runningSpeed, Boolean basalEnergyBurned, Boolean activeEnergyBurned, Boolean sleepAnalysis, Boolean heartRate, Boolean oxygenSaturation, Boolean bloodPressureSystolic, Boolean bloodPressureDiastolic, Boolean respiratoryRate, Boolean bodyTemperature, Boolean ecgData, Boolean watchDeviceLatitude, Boolean watchDeviceLongitude) {
        this.stepCount = stepCount;
        this.runningSpeed = runningSpeed;
        this.basalEnergyBurned = basalEnergyBurned;
        this.activeEnergyBurned = activeEnergyBurned;
        this.sleepAnalysis = sleepAnalysis;
        this.heartRate = heartRate;
        this.oxygenSaturation = oxygenSaturation;
        this.bloodPressureSystolic = bloodPressureSystolic;
        this.bloodPressureDiastolic = bloodPressureDiastolic;
        this.respiratoryRate = respiratoryRate;
        this.bodyTemperature = bodyTemperature;
        this.ecgData = ecgData;
        this.watchDeviceLatitude = watchDeviceLatitude;
        this.watchDeviceLongitude = watchDeviceLongitude;
    }

    public void updateBy(HealthDataFlag projectFlag) {
        if (projectFlag.stepCount) this.stepCount = true;
        if (projectFlag.runningSpeed) this.runningSpeed = true;
        if (projectFlag.basalEnergyBurned) this.basalEnergyBurned = true;
        if (projectFlag.activeEnergyBurned) this.activeEnergyBurned = true;
        if (projectFlag.sleepAnalysis) this.sleepAnalysis = true;
        if (projectFlag.heartRate) this.heartRate = true;
        if (projectFlag.oxygenSaturation) this.oxygenSaturation = true;
        if (projectFlag.bloodPressureSystolic) this.bloodPressureSystolic = true;
        if (projectFlag.bloodPressureDiastolic) this.bloodPressureDiastolic = true;
        if (projectFlag.respiratoryRate) this.respiratoryRate = true;
        if (projectFlag.bodyTemperature) this.bodyTemperature = true;
        if (projectFlag.ecgData) this.ecgData = true;
        if (projectFlag.watchDeviceLatitude) this.watchDeviceLatitude = true;
        if (projectFlag.watchDeviceLongitude) this.watchDeviceLongitude = true;
    }
}
