package com.example.web_ban_banh.Controller.Order_Controller;

import com.example.web_ban_banh.DTO.Order_DTO.CheckOutRequest.CheckOutRequestDTO;
import com.example.web_ban_banh.DTO.Order_DTO.CheckOutResponese.CheckOutResponseDTO;
import com.example.web_ban_banh.Exception.BadRequestEx_400.BadRequestExceptionCustom;
import com.example.web_ban_banh.Service.Order_Service.Order_ServicenIn;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/order")
@Validated
public class CheckOut {
    private Order_ServicenIn orderService;

    @Autowired
    public CheckOut(Order_ServicenIn orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout/{id}")
    public ResponseEntity<?>checkout(@RequestBody @Valid CheckOutRequestDTO request, @PathVariable @Min(value = 1,message = "ID phải lớn hơn 0")int id){
        try {
            CheckOutResponseDTO dto=orderService.processCheckout(id,request);
            return ResponseEntity.ok(Map.of("message","Đơn hàng được tạo thành công",
                                            "Thông tin đơn hàng",dto));
        }catch(Exception e){
            // Log lỗi để debug
            System.err.println("Lỗi tạo đơn hàng: " + e.getMessage());
            e.printStackTrace();

            // Throw exception phù hợp với người dùng
            throw new BadRequestExceptionCustom("Không thể tạo đơn hàng. Vui lòng thử lại sau");
        }

    }
}
