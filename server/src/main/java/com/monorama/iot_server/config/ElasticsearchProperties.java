package com.monorama.iot_server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "external.elasticsearch")
public class ElasticsearchProperties {
    private String url;
    private String username;
    private String password;
}