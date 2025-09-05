package com.example.web_ban_banh.Exception.NotFoundEx_404;

import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler_404 {
    @ExceptionHandler(NotFoundExceptionCustom.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundExceptionCustom ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
