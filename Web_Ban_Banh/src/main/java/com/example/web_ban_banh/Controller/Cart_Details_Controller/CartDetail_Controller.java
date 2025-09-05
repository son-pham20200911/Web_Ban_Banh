package com.example.web_ban_banh.Controller.Cart_Details_Controller;

import com.example.web_ban_banh.Service.Cart_Details_Service.Cart_Details_ServiceIn;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cartdetails")
@Validated
public class CartDetail_Controller {
    private Cart_Details_ServiceIn cartDetailsService;

    @Autowired
    public CartDetail_Controller(Cart_Details_ServiceIn cartDetailsService) {
        this.cartDetailsService = cartDetailsService;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCartDetails(@PathVariable @Min(value = 1, message = "ID phải lớn hơn 0") int id) {
        cartDetailsService.deleteCartDetail(id);
        return ResponseEntity.ok("Đã xóa thành công");
    }
}
