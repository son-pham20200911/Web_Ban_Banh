package com.example.web_ban_banh.Controller.Cart_Details_Controller;

import com.example.web_ban_banh.DTO.Cart_Details_DTO.Get.Cart_Details_Display_DTO;
import com.example.web_ban_banh.DTO.Cart_Details_DTO.Update.UpdateCartDetaisl_DTO;
import com.example.web_ban_banh.Service.Cart_Details_Service.Cart_Details_ServiceIn;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cartdetails")
@Validated
public class CartDetail_Controller {
    private Cart_Details_ServiceIn cartDetailsService;

    @Autowired
    public CartDetail_Controller(Cart_Details_ServiceIn cartDetailsService) {
        this.cartDetailsService = cartDetailsService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?>updateCartDetails(@PathVariable int id, @RequestBody @Valid UpdateCartDetaisl_DTO update){
        Cart_Details_Display_DTO dto= cartDetailsService.updateCartDetails(id,update);
        return ResponseEntity.ok("Cập nhật thành công");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCartDetails(@PathVariable @Min(value = 1, message = "ID phải lớn hơn 0") int id) {
        cartDetailsService.deleteCartDetail(id);
        return ResponseEntity.ok("Đã xóa thành công");
    }

}
