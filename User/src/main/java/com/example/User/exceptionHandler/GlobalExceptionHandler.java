package com.example.User.exceptionHandler;

import com.example.User.customExceptions.InvalidJwtException;
import com.example.User.customExceptions.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Handles UserAlreadyExistsException and returns a response with status 409 Conflict.
     *
     * @param ex the exception that was thrown
     * @return a ResponseEntity containing the error details
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUserExists(UserAlreadyExistsException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", HttpStatus.CONFLICT.value());
        error.put("error", "Conflict");
        error.put("message", ex.getMessage());
        error.put("timestamp", Instant.now().toString());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Handles BadRequestException and returns a response with status 400 Bad Request.
     *
     * @param ex the exception that was thrown
     * @return a ResponseEntity containing the error details
     */
   @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Bad Request");
        error.put("message", "Validation failed: " + ex.getBindingResult().getFieldError().getDefaultMessage());
        error.put("timestamp", Instant.now().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles InvalidJwtException and returns a response with status 401 Unauthorized.
     *
     * @param ex the exception that was thrown
     * @return a ResponseEntity containing the error details
     */
    @ExceptionHandler(InvalidJwtException.class)
    public ResponseEntity<?> handleInvalidJwt(InvalidJwtException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "INVALID_JWT");
        body.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    /**
     * Handles HttpClientErrorException and returns a response with status 400 Bad Request.
     *
     * @param ex the exception that was thrown
     * @return a ResponseEntity containing the error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.put("error", "Internal Server Error");
        error.put("message", ex.getMessage());
        error.put("timestamp", Instant.now().toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

}
