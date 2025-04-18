package com.necrock.readingtracker.api.error;

import com.necrock.readingtracker.service.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static com.necrock.readingtracker.api.error.ErrorType.INTERNAL_ERROR;
import static com.necrock.readingtracker.api.error.ErrorType.NOT_FOUND_ERROR;
import static com.necrock.readingtracker.api.error.ErrorType.VALIDATION_ERROR;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        for (var fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }

        var apiError = new ApiError(VALIDATION_ERROR, "Validation failed", errors);
        return ResponseEntity.status(BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
        var apiError = new ApiError(NOT_FOUND_ERROR, ex.getMessage());
        return ResponseEntity.status(NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneralError(Exception ex) {
        var apiError = new ApiError(INTERNAL_ERROR, ex.getMessage());
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(apiError);
    }
}
