package com.necrock.readingtracker.exception.handler;

import com.necrock.readingtracker.exception.AlreadyExistsException;
import com.necrock.readingtracker.exception.NotFoundException;
import com.necrock.readingtracker.exception.UnauthorizedException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

import static com.necrock.readingtracker.exception.handler.ErrorType.ALREADY_EXISTS_ERROR;
import static com.necrock.readingtracker.exception.handler.ErrorType.INTERNAL_ERROR;
import static com.necrock.readingtracker.exception.handler.ErrorType.NOT_FOUND_ERROR;
import static com.necrock.readingtracker.exception.handler.ErrorType.UNAUTHORIZED_ERROR;
import static com.necrock.readingtracker.exception.handler.ErrorType.VALIDATION_ERROR;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

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

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiError> handleNoHandlerFound(NoHandlerFoundException ex) {
        var apiError = new ApiError(NOT_FOUND_ERROR, ex.getMessage());
        return ResponseEntity.status(NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ApiError> handleAlreadyExists(AlreadyExistsException ex) {
        var apiError = new ApiError(ALREADY_EXISTS_ERROR, ex.getMessage());
        return ResponseEntity.status(CONFLICT).body(apiError);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorized(UnauthorizedException ex) {
        var apiError = new ApiError(UNAUTHORIZED_ERROR, ex.getMessage());
        return ResponseEntity.status(UNAUTHORIZED).body(apiError);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthentication(AuthenticationException ex) {
        var apiError = new ApiError(UNAUTHORIZED_ERROR, ex.getMessage());
        return ResponseEntity.status(UNAUTHORIZED).body(apiError);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex) {
        var apiError = new ApiError(UNAUTHORIZED_ERROR, ex.getMessage());
        return ResponseEntity.status(FORBIDDEN).body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneralError(Exception ex) {
        var apiError = new ApiError(INTERNAL_ERROR, ex.getMessage());
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(apiError);
    }
}
