package com.monorama.iot_server.repository;

import com.monorama.iot_server.domain.HealthData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HDRepository extends JpaRepository<HealthData, Long> {
}
