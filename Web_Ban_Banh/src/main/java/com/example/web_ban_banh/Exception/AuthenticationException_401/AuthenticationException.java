package com.example.web_ban_banh.Exception.AuthenticationException_401;

import com.example.web_ban_banh.Exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class AuthenticationException {
    @ExceptionHandler(javax.security.sasl.AuthenticationException.class)
    public ResponseEntity<?> handleUnauthor(javax.security.sasl.AuthenticationException ex, HttpServletRequest request){
        ErrorResponse error=new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Xác thực thất bại. Vui lòng đăng nhập lại",
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(401).body(error);
    }
    //Bắt lỗi 401(lỗi khi nhập sai Username hoặc Password) và hiển thị theo dạng của class "ErrorResponse":


    //Muốn dùng "BadCredentialsException" và "InsufficientAuthenticationException" → Cần dependency Spring Security

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException ex,HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Tên đăng nhập hoặc mật khẩu không chính xác",
                 request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(401).body(error);
    }


    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<?> handleInsufficientAuth(InsufficientAuthenticationException ex,HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại",
                 request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(401).body(error);
    }
}
