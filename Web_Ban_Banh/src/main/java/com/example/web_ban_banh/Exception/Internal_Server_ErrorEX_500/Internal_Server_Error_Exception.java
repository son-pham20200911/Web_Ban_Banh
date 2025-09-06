package com.example.web_ban_banh.Exception.Internal_Server_ErrorEX_500;

import com.example.web_ban_banh.Exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class Internal_Server_Error_Exception {
    private static final Logger log = LoggerFactory.getLogger(Internal_Server_Error_Exception.class);
    // Xử lý lỗi kết nối CSDL
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<?>handleDataAccessException(DataAccessException ex, HttpServletRequest request){
        log.error("Data Access Exception",ex);
        ErrorResponse error=new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Lỗi kết nối cơ sở dữ liệu. Vui lòng thử lại sau "+ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
    return ResponseEntity.status(500).body(error);
    }

    // Xử lý lỗi NullPointer
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?>handleNullPointerException(NullPointerException ex,HttpServletRequest request){
        log.error("Null Pointer Exception ",ex);
        ErrorResponse error=new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Lỗi xử lý dữ liệu"+ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(500).body(error);
    }


//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<?>hadleException(Exception ex, HttpServletRequest request){
//        // Log lỗi để debug
//        log.error("Internal server error occurred: ", ex);
//        ErrorResponse error=new ErrorResponse(
//                HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                "Lỗi hệ thống: "+ex.getMessage(),
//                request.getRequestURI(),
//                LocalDateTime.now()
//        );
//        return ResponseEntity.status(500).body(error);
//    }
    //Bắt lỗi 500(lỗi bên server) và hiển thị theo dạng của class "ErrorResponse":



    //"status": 500,
    //"message":"Lỗi hệ thống:....",
    //"path": đường dẫn,
    //"timestamp":"2025-07-28T22:16:00"

    //Còn có các lỗi cụ thể khác như:
    //     DataAccessException.class  :"Lỗi kết nối cơ sở dữ liệu. Vui lòng thử lại sau"
    //     SQLException.class         :"Lỗi truy vấn dữ liệu"
    //     NullPointerException.class :"Lỗi xử lý dữ liệu"

}
