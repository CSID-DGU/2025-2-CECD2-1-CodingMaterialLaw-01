package com.monorama.iot_server.dto.request.airquality;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.monorama.iot_server.domain.AirQualityData;
import com.monorama.iot_server.domain.User;
import com.monorama.iot_server.domain.embedded.AirQualityDataFlag;
import com.monorama.iot_server.domain.embedded.AirQualityDataItem;
import com.monorama.iot_server.exception.CommonException;
import com.monorama.iot_server.exception.ErrorCode;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record AQDRequestDto(
        @NotNull
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime createdAt,

        Double pm25Value,
        Integer pm25Level,
        Double pm10Value,
        Integer pm10Level,
        Double temperature,
        Integer temperatureLevel,
        Double humidity,
        Integer humidityLevel,
        Double co2Value,
        Integer co2Level,
        Double vocValue,
        Integer vocLevel,
        Double picoDeviceLatitude,
        Double picoDeviceLongitude
) {
    public AirQualityData toEntity(User user) {
        AirQualityData entity = new AirQualityData();
        entity.setCreatedAt(this.createdAt);
        entity.setAirQualityDataItem(
                AirQualityDataItem.builder()
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
                        .build()
        );
        entity.setUser(user);
        return entity;
    }

    public List<String> getMissingFields(User user) {
        if (user.getUserDataPermission() == null) {
            throw new CommonException(ErrorCode.NOT_FOUND_RESOURCE);
        }

        List<String> missingFields = new ArrayList<>();
        AirQualityDataFlag flag = user.getUserDataPermission().getAirQualityDataFlag();

        if (Boolean.TRUE.equals(flag.getPm25Value()) && pm25Value == null) missingFields.add("pm25Value");
        if (Boolean.TRUE.equals(flag.getPm25Level()) && pm25Level == null) missingFields.add("pm25Level");
        if (Boolean.TRUE.equals(flag.getPm10Value()) && pm10Value == null) missingFields.add("pm10Value");
        if (Boolean.TRUE.equals(flag.getPm10Level()) && pm10Level == null) missingFields.add("pm10Level");
        if (Boolean.TRUE.equals(flag.getTemperature()) && temperature == null) missingFields.add("temperature");
        if (Boolean.TRUE.equals(flag.getTemperatureLevel()) && temperatureLevel == null) missingFields.add("temperatureLevel");
        if (Boolean.TRUE.equals(flag.getHumidity()) && humidity == null) missingFields.add("humidity");
        if (Boolean.TRUE.equals(flag.getHumidityLevel()) && humidityLevel == null) missingFields.add("humidityLevel");
        if (Boolean.TRUE.equals(flag.getCo2Value()) && co2Value == null) missingFields.add("co2Value");
        if (Boolean.TRUE.equals(flag.getCo2Level()) && co2Level == null) missingFields.add("co2Level");
        if (Boolean.TRUE.equals(flag.getVocValue()) && vocValue == null) missingFields.add("vocValue");
        if (Boolean.TRUE.equals(flag.getVocLevel()) && vocLevel == null) missingFields.add("vocLevel");
        if (Boolean.TRUE.equals(flag.getPicoDeviceLatitude()) && picoDeviceLatitude == null) missingFields.add("picoDeviceLatitude");
        if (Boolean.TRUE.equals(flag.getPicoDeviceLongitude()) && picoDeviceLongitude == null) missingFields.add("picoDeviceLongitude");

        return missingFields;
    }
}