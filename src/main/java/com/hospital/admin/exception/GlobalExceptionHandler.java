package com.hospital.admin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;


import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.hospital.admin.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(
                ApiResponse.builder().success(false).message(ex.getMessage()).data(null).build(),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<?>> handleValidation(ValidationException ex) {
        return new ResponseEntity<>(
                ApiResponse.builder().success(false).message(ex.getMessage()).data(null).build(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<?>> handleConflict(ConflictException ex) {
        return new ResponseEntity<>(
                ApiResponse.builder().success(false).message(ex.getMessage()).data(null).build(),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<?>> handleForbidden(ForbiddenException ex) {
        return new ResponseEntity<>(
                ApiResponse.builder().success(false).message(ex.getMessage()).data(null).build(),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
    public ResponseEntity<ApiResponse<?>> handleAuthentication(Exception ex) {
        return new ResponseEntity<>(
                ApiResponse.builder().success(false).message("Authentication failed: " + ex.getMessage()).data(null).build(),
                HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiResponse<?>> handleOptimisticLocking(ObjectOptimisticLockingFailureException ex) {
        String message = "CONCURRENCY CONFLICT: This blood component has been modified or reserved by another user. Please refresh and try again.";
        return new ResponseEntity<>(
                ApiResponse.builder().success(false).message(message).data(null).build(),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneric(Exception ex) {
        return new ResponseEntity<>(
                ApiResponse.builder().success(false).message(ex.getMessage()).data(null).build(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
