package com.monorama.iot_server.security;
import com.monorama.iot_server.dto.ExceptionDto;
import com.monorama.iot_server.exception.ErrorCode;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletResponse;
import net.minidev.json.JSONValue;

import java.util.HashMap;
import java.util.Map;
public abstract class AbstractAuthenticationFailure {
    protected void setErrorResponse(
            HttpServletResponse response,
            ErrorCode errorCode) throws IOException, java.io.IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(errorCode.getHttpStatus().value());

        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("data", null);
        result.put("error", new ExceptionDto(errorCode, errorCode.getMessage()));

        response.getWriter().write(JSONValue.toJSONString(result));
    }
}
