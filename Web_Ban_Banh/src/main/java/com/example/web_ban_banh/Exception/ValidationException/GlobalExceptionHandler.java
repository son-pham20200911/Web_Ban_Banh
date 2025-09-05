package com.example.web_ban_banh.Exception.ValidationException;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String,String>> handleConstraintViolation(ConstraintViolationException ex){
        Map<String,String>errors=new HashMap<>();
        ex.getConstraintViolations().forEach(Violation->{
            String field= Violation.getPropertyPath().toString();
            String message= Violation.getMessage();
            errors.put(field,message);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    //Hiển thị ra thông báo khi dữ liệu đầu vào không đáp ứng đúng yêu cầu(Dùng với các annotation @Min @Max @Length @Size,... ở DTO)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>>handleValidationErrors(MethodArgumentNotValidException ex){
        Map<String,String>errors=new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error->{
            errors.put(error.getField(),error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
