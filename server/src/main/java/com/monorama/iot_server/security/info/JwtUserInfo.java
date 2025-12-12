package com.monorama.iot_server.security.info;

import com.monorama.iot_server.type.ERole;

public record JwtUserInfo(Long id,ERole role) {

}
