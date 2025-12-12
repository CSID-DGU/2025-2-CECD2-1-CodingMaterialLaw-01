package com.monorama.iot_server.dto.request.auth;

import jakarta.validation.constraints.NotNull;

public record GoogleLoginRequestDto(
        @NotNull String idToken
) {
}
