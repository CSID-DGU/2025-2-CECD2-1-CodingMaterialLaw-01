package com.monorama.iot_server.dto.request.auth;

import jakarta.validation.constraints.NotNull;

public record AppleLoginRequestDto(
        @NotNull String identityToken
) {
}
