package com.example.web_ban_banh.Exception.BadRequestEx_400;

import com.example.web_ban_banh.Exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionHandler_400 {
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleInvalidJson(HttpMessageNotReadableException ex, HttpServletRequest request) {
        ErrorResponse er = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "JSON không hợp lệ" + ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(400).body(er);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?>handleTypeMismatch(MethodArgumentTypeMismatchException ex,HttpServletRequest request){
       ErrorResponse er=new ErrorResponse(
               HttpStatus.BAD_REQUEST.value(),
               "Kiểu dữ liệu không đúng cho tham số "+ex.getName(),
               request.getRequestURI(),
               LocalDateTime.now()
       );
       return ResponseEntity.status(400).body(er);
    }

    // Xử lý lỗi SQL
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<?> handleSQLException(SQLException ex,HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Lỗi truy vấn dữ liệu: " + ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(400).body(error);
    }
}