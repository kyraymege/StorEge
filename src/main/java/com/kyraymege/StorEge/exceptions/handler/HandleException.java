package com.kyraymege.StorEge.exceptions.handler;

import com.kyraymege.StorEge.domain.Response;
import com.kyraymege.StorEge.exceptions.APIException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.stream.Collectors;

import static com.kyraymege.StorEge.utils.RequestUtils.handleErrorResponse;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class HandleException extends ResponseEntityExceptionHandler implements ErrorController {
    private final HttpServletRequest request;

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest webRequest) {
        log.error("handleExceptionInternal: " + ex.getMessage() + " at " + request.getRequestURI());
        return new ResponseEntity<>(handleErrorResponse(ex.getMessage(),getRootCauseMessage(ex),request, statusCode), statusCode);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest webRequest) {
        log.error("handleMethodArgumentNotValid: " + ex.getMessage() + " at " + request.getRequestURI());
        var fieldErrors = ex.getBindingResult().getFieldErrors();
        var fieldsMessage = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));
        return new ResponseEntity<>(handleErrorResponse("Validation failed", fieldsMessage, request, status), status);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<Response> apiException(APIException ex) {
        log.error("handleAPIException: " + ex.getMessage() + " at " + request.getRequestURI());
        return new ResponseEntity<>(handleErrorResponse(ex.getMessage(),getRootCauseMessage(ex),request, BAD_REQUEST), BAD_REQUEST);
    }


//    IT CAN SPECIFY THE EXCEPTIONS TO BE HANDLED
//    @ExceptionHandler(BadCredentialsException.class)
//    public ResponseEntity<Response> badCredentialsException(BadCredentialsException ex) {
//        log.error("badCredentialsException: " + ex.getMessage() + " at " + request.getRequestURI());
//        return new ResponseEntity<>(handleErrorResponse(ex.getMessage(),getRootCauseMessage(ex),request, BAD_REQUEST), BAD_REQUEST);
//    }
//
//    @ExceptionHandler(AccessDeniedException.class)
//    public ResponseEntity<Response> accessDeniedException(AccessDeniedException ex) {
//        log.error("accessDeniedException: " + ex.getMessage() + " at " + request.getRequestURI());
//        return new ResponseEntity<>(handleErrorResponse(ex.getMessage(), getRootCauseMessage(ex), request, FORBIDDEN), FORBIDDEN);
//    }
//
//    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
//    public ResponseEntity<Response> sqlIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException ex) {
//        log.error("sqlIntegrityConstraintViolationException: " + ex.getMessage() + " at " + request.getRequestURI());
//        return new ResponseEntity<>(handleErrorResponse(ex.getMessage().contains("Duplicate entry") ? "Information already exists": ex.getMessage(), getRootCauseMessage(ex), request, NOT_FOUND), NOT_FOUND);
//    }
//
//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<Response> illegalArgumentException(IllegalArgumentException ex) {
//        log.error("handleAPIException: " + ex.getMessage() + " at " + request.getRequestURI());
//        return new ResponseEntity<>(handleErrorResponse(ex.getMessage(), getRootCauseMessage(ex), request, BAD_REQUEST), BAD_REQUEST);
//    }

}
