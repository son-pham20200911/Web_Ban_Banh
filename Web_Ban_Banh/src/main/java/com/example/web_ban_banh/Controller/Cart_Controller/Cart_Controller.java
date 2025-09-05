package com.example.web_ban_banh.Controller.Cart_Controller;

import com.example.web_ban_banh.DTO.Cart_DTO.Create.Create_CartDTO;
import com.example.web_ban_banh.DTO.Cart_DTO.Get.Cart_DTO;
import com.example.web_ban_banh.Service.Cart_Service.Cart_ServiceIn;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@Validated
public class Cart_Controller {
    private Cart_ServiceIn cartService;

    @Autowired
    public Cart_Controller(Cart_ServiceIn cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> addItem (@PathVariable int id,@RequestBody @Valid Create_CartDTO dto){
        Cart_DTO cartDTO=cartService.addItem(id,dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Đã thêm sản phẩm thành công");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?>deleteCart(@PathVariable int id){
        cartService.deleteCart(id);
        return ResponseEntity.ok("Đã xóa thành công giỏ Hàng");
    }
}
