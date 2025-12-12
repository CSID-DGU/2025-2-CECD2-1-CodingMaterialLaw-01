package com.monorama.iot_server.domain.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class AirQualityDataItem {

    @Column(name = "pm25_value", nullable = false)
    private Double pm25Value;

    @Column(name = "pm25_level", nullable = false)
    private Integer pm25Level;

    @Column(name = "pm10_value", nullable = false)
    private Double pm10Value;

    @Column(name = "pm10_level", nullable = false)
    private Integer pm10Level;

    @Column(name = "temperature", nullable = false)
    private Double temperature;

    @Column(name = "temperature_level", nullable = false)
    private Integer temperatureLevel;

    @Column(name = "humidity", nullable = false)
    private Double humidity;

    @Column(name = "humidity_level", nullable = false)
    private Integer humidityLevel;

    @Column(name = "co2_value", nullable = false)
    private Double co2Value;

    @Column(name = "co2_level", nullable = false)
    private Integer co2Level;

    @Column(name = "voc_value", nullable = false)
    private Double vocValue;

    @Column(name = "voc_level", nullable = false)
    private Integer vocLevel;

    @Column(name = "pico_device_latitude", nullable = false)
    private Double picoDeviceLatitude;

    @Column(name = "pico_device_longitude", nullable = false)
    private Double picoDeviceLongitude;

    /*** constructor ***/
    @Builder
    public AirQualityDataItem(Double pm25Value, Integer pm25Level, Double pm10Value, Integer pm10Level, Double temperature, Integer temperatureLevel, Double humidity, Integer humidityLevel, Double co2Value, Integer co2Level, Double vocValue, Integer vocLevel, Double picoDeviceLatitude, Double picoDeviceLongitude) {
        this.pm25Value = pm25Value;
        this.pm25Level = pm25Level;
        this.pm10Value = pm10Value;
        this.pm10Level = pm10Level;
        this.temperature = temperature;
        this.temperatureLevel = temperatureLevel;
        this.humidity = humidity;
        this.humidityLevel = humidityLevel;
        this.co2Value = co2Value;
        this.co2Level = co2Level;
        this.vocValue = vocValue;
        this.vocLevel = vocLevel;
        this.picoDeviceLatitude = picoDeviceLatitude;
        this.picoDeviceLongitude = picoDeviceLongitude;
    }
}

