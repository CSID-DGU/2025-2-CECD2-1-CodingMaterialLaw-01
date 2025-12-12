package com.monorama.iot_server.repository;

import com.monorama.iot_server.domain.AirQualityData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AQDRepository extends JpaRepository<AirQualityData, Long> {
}