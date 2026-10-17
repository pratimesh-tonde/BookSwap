package com.bookexchange.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/** Converts every exception into a clean JSON body so stack traces never reach the client. */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApi(ApiException e) {
        return build(e.getStatus(), e.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fe : e.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(fe.getField(), fe.getDefaultMessage());
        }
        String first = errors.isEmpty() ? "Invalid request" : errors.values().iterator().next();
        return build(HttpStatus.BAD_REQUEST, first, errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadable(HttpMessageNotReadableException e) {
        return build(HttpStatus.BAD_REQUEST, "Malformed request body", null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleDenied(AccessDeniedException e) {
        return build(HttpStatus.FORBIDDEN, "You do not have permission to do this", null);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleTooLarge(MaxUploadSizeExceededException e) {
        return build(HttpStatus.BAD_REQUEST, "Image is too large (max 5 MB)", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleOther(Exception e) {
        // Spring MVC's own errors (unknown route, wrong method...) implement ErrorResponse - keep their status.
        if (e instanceof ErrorResponse er) {
            return build(er.getStatusCode(), er.getBody().getDetail(), null);
        }
        log.error("Unexpected error", e);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong on the server", null);
    }

    private ResponseEntity<Map<String, Object>> build(HttpStatusCode status, String message, Map<String, String> errors) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("message", message);
        if (errors != null) {
            body.put("errors", errors);
        }
        return ResponseEntity.status(status).body(body);
    }
}
