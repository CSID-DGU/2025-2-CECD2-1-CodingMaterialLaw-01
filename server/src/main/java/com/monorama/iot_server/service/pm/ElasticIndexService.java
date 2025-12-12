package com.monorama.iot_server.service.pm;

import com.monorama.iot_server.config.ElasticsearchProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ElasticIndexService {

    private final ElasticsearchProperties esProps;
    private final RestTemplate restTemplate;

    public void createHealthIndex(String indexIdentifier) {

        String indexName = "index-health" + indexIdentifier; // index-health-{userId}-{projectId}-{endDate}

        Map<String, Object> mappings = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();
        List<String> healthFields = extractHealthFields();
        List<String> personalFields = extractPersonalFields();
//        List<String> healthFlagFields = extractHealthFlagFields();
//        List<String> personalFlagFields = extractPersonalFLagFields();

        properties.put("userId", Map.of("type", "long"));
        properties.put("location", Map.of("type", "geo_point"));
        properties.put("timestamp", Map.of(
                "type", "date",
                "format", "strict_date_optional_time||epoch_second"
        ));

        for (String field : healthFields) {
            switch (field) {
                case "sleep_analysis", "ecg_data", "title" -> properties.put(field, Map.of("type", "keyword"));
                default -> properties.put(field, Map.of("type", "double"));
            }
        }

        for (String field : personalFields) {
            switch (field) {
                case "height", "weight" -> properties.put(field, Map.of("type", "double"));
                case "date_of_birth" -> properties.put(field, Map.of("type", "date"));
                default -> properties.put(field, Map.of("type", "keyword"));
            }
        }

//        for (String field : healthFlagFields) {
//            properties.put(field, Map.of("type", "boolean"));
//        }
//
//        for (String field : personalFlagFields) {
//            properties.put(field, Map.of("type", "boolean"));
//        }

        mappings.put("properties", properties);
        Map<String, Object> payload = Map.of("mappings", mappings);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        setBasicAuthHeader(headers);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        String esUrl = esProps.getUrl() + "/" + indexName;
        ResponseEntity<String> response = restTemplate.exchange(esUrl, HttpMethod.PUT, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Index creation failed: " + response.getBody());
        }
    }

    public void createAirIndex(String indexIdentifier) {

        String indexName = "index-air" + indexIdentifier; // index-air-{userId}-{projectId}-{endDate}

        Map<String, Object> mappings = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();
        List<String> airFields = extractAirFields();
        List<String> personalFields = extractPersonalFields();
//        List<String> airFlagFields = extractAirFlagFields();
//        List<String> personalFlagFields = extractPersonalFLagFields();

        properties.put("userId", Map.of("type", "long"));
        properties.put("location", Map.of("type", "geo_point"));
        properties.put("created_at", Map.of(
                "type", "date",
                "format", "strict_date_optional_time||epoch_second"
        ));

        for (String field : airFields) {
            if (field.endsWith("_level")) {
                properties.put(field, Map.of("type", "integer"));
            } else {
                properties.put(field, Map.of("type", "double"));
            }
        }

        for (String field : personalFields) {
            switch (field) {
                case "height", "weight" -> properties.put(field, Map.of("type", "double"));
                case "date_of_birth" -> properties.put(field, Map.of("type", "date"));
                default -> properties.put(field, Map.of("type", "keyword"));
            }
        }

//        for (String field : airFlagFields) {
//            properties.put(field, Map.of("type", "boolean"));
//        }
//
//        for (String field : personalFlagFields) {
//            properties.put(field, Map.of("type", "boolean"));
//        }

        mappings.put("properties", properties);
        Map<String, Object> payload = Map.of("mappings", mappings);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        setBasicAuthHeader(headers);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        String esUrl = esProps.getUrl() + "/" + indexName;
        ResponseEntity<String> response = restTemplate.exchange(esUrl, HttpMethod.PUT, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Index creation failed: " + response.getBody());
        }
    }

    private void setBasicAuthHeader(HttpHeaders headers) {
        String auth = esProps.getUsername() + ":" + esProps.getPassword();
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + encodedAuth);
    }

    private List<String> extractHealthFields() {
        return List.of(
                "title",
                "step_count",
                "running_speed",
                "basal_energy_burned",
                "active_energy_burned",
                "sleep_analysis",
                "heart_rate",
                "oxygen_saturation",
                "blood_pressure_systolic",
                "blood_pressure_diastolic",
                "respiratory_rate",
                "body_temperature",
                "ecg_data",
                "watch_device_latitude",
                "watch_device_longitude"
        );
    }

    private List<String> extractAirFields() {
        return List.of(
                "title",
                "pm25_value",
                "pm25_level",
                "pm10_value",
                "pm10_level",
                "temperature",
                "temperature_level",
                "humidity",
                "humidity_level",
                "co2_value",
                "co2_level",
                "voc_value",
                "voc_level",
                "pico_device_latitude",
                "pico_device_longitude"
        );
    }

    private List<String> extractPersonalFields() {
        return List.of(
                "name",
                "email",
                "gender",
                "national_code",
                "phone_number",
                "date_of_birth",
                "blood_type",
                "weight",
                "height"
        );
    }

//    private List<String> extractPersonalFLagFields() {
//        return List.of(
//                "name_flag",
//                "email_flag",
//                "gender_flag",
//                "national_code_flag",
//                "phone_number_flag",
//                "date_of_birth_flag",
//                "blood_type_flag",
//                "height_flag",
//                "weight_flag"
//        );
//    }
//
//    private List<String> extractHealthFlagFields() {
//        return List.of(
//                "step_count_flag",
//                "running_speed_flag",
//                "basal_energy_burned_flag",
//                "active_energy_burned_flag",
//                "sleep_analysis_flag",
//                "heart_rate_flag",
//                "oxygen_saturation_flag",
//                "blood_pressure_systolic_flag",
//                "blood_pressure_diastolic_flag",
//                "respiratory_rate_flag",
//                "body_temperature_flag",
//                "ecg_data_flag",
//                "watch_device_latitude_flag",
//                "watch_device_longitude_flag"
//        );
//    }
//
//    private List<String> extractAirFlagFields() {
//        return List.of(
//                "pm25_value_flag",
//                "pm25_level_flag",
//                "pm10_value_flag",
//                "pm10_level_flag",
//                "temperature_flag",
//                "temperature_level_flag",
//                "humidity_flag",
//                "humidity_level_flag",
//                "co2_value_flag",
//                "co2_level_flag",
//                "voc_value_flag",
//                "voc_level_flag",
//                "pico_device_latitude_flag",
//                "pico_device_longitude_flag"
//        );
//    }

    // TODO: air meta data index create method
    // TODO: 프로젝트 참여 user 별 만들어주기
    // TODO: 스케쥴러로 index 관리 (삭제)
}
