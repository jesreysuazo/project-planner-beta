package com.example.project_planner_beta.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public ResponseEntity<Map<String,Object>> buildResponse(
            HttpStatus status, String message, String path
    ){
        Map<String,Object> errorBody = new HashMap<>();
        errorBody.put("timestamp",LocalDateTime.now());
        errorBody.put("status", status.value());
        errorBody.put("error",status.getReasonPhrase());
        errorBody.put("message", message);
        errorBody.put("path", path);

        return new ResponseEntity<>(errorBody,status);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleNotFound(
            NotFoundException ex, HttpServletRequest request
    ){
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String,Object>> handleNotFound(
            BadRequestException ex, HttpServletRequest request
    ){
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String,Object>> handleNotFound(
            RuntimeException ex, HttpServletRequest request
    ){
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,"An unexpected error occured. " + ex.getMessage(), request.getRequestURI());
    }
}
