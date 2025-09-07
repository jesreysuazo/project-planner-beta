package com.example.project_planner_beta.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // put error message on res
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String,Object>> handleBadRequest(BadRequestException ex){
        Map<String,Object> errorBody = new HashMap<>();
        errorBody.put("timestamp",LocalDateTime.now());
        errorBody.put("status", HttpStatus.BAD_REQUEST.value());
        errorBody.put("error","Bad Request");
        errorBody.put("message", ex.getMessage());
        return new ResponseEntity<>(errorBody,HttpStatus.BAD_REQUEST);
    }
}
