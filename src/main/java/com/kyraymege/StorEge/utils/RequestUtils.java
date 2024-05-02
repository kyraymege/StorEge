package com.kyraymege.StorEge.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyraymege.StorEge.domain.Response;
import com.kyraymege.StorEge.exceptions.APIException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.util.BiConsumer;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.function.BiFunction;

import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyMap;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class RequestUtils {

    private static final BiConsumer<HttpServletResponse, Response> writeResponse = ((httpServletResponse, response) -> {
        try {
            var outputStream = httpServletResponse.getOutputStream();
            new ObjectMapper().writeValue(outputStream, response);
            outputStream.flush();
        } catch (Exception e) {
            throw new APIException(e.getMessage());
        }
    }
    );

    private static final BiFunction<Exception, HttpStatus, String> errorReason = ((exception, httpStatus) -> {
        if (httpStatus.isSameCodeAs(FORBIDDEN)) {
            return "You don't have permission to access this resource";
        }
        if (httpStatus.isSameCodeAs(UNAUTHORIZED)) {
            return "You are not authorized to access this resource. Please login to continue";
        }
        if (exception instanceof DisabledException ||
                exception instanceof LockedException ||
                exception instanceof BadCredentialsException ||
                exception instanceof CredentialsExpiredException ||
                exception instanceof APIException) {
            return exception.getMessage();
        }
        if (httpStatus.is5xxServerError()) {
            return "An internal server error occurred while processing your request.";
        } else {
            return "An error occurred while processing your request. Please try again later";
        }
    });

    public static Response getResponse(HttpServletRequest request, Map<?, ?> data, String message, HttpStatus status) {
        return new Response(now().toString(), status.value(), request.getRequestURI(), valueOf(status.value()), message, EMPTY, data);
    }

    public static void handleErrorResponse(HttpServletRequest request, HttpServletResponse response, Exception e) {
        if (e instanceof AccessDeniedException) {
            Response apiResponse = getErrorResponse(request, response, e, HttpStatus.FORBIDDEN);
            writeResponse.accept(response, apiResponse);
        }
    }

    private static Response getErrorResponse(HttpServletRequest request, HttpServletResponse response, Exception e, HttpStatus status) {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(status.value());
        return new Response(now().toString(), status.value(), request.getRequestURI(), valueOf(status.value()), errorReason.apply(e,status), getRootCauseMessage(e), emptyMap());
    }
}
