package com.example.web_ban_banh.Exception.AccessDeniedException_403;

import com.example.web_ban_banh.Exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class AccessDeniedException {
    @ExceptionHandler(java.nio.file.AccessDeniedException.class)
    public ResponseEntity<?> handleForbidden(java.nio.file.AccessDeniedException ex, HttpServletRequest request){
        ErrorResponse error=new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Truy cập bị từ chối. Bạn không có quyền thực hiện thao tác này",
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(403).body(error);
    }
    //Bắt lỗi 403(lỗi khi người dùng sử dụng các chức năng không được phân quyền) và hiển thị theo dạng của class "ErrorResponse":
}
