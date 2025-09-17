package com.example.web_ban_banh.Controller.Cart_Controller;

import com.example.web_ban_banh.DTO.Cart_DTO.Create.Create_CartDTO;
import com.example.web_ban_banh.DTO.Cart_DTO.Get.Cart_DTO;
import com.example.web_ban_banh.DTO.Cart_DTO.Get.Display_Cart_All_DTO;
import com.example.web_ban_banh.DTO.Cart_DTO.Get.Display_Cart_DTO;
import com.example.web_ban_banh.Service.Cart_Service.Cart_ServiceIn;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@Validated
public class Cart_Controller {
    private Cart_ServiceIn cartService;

    @Autowired
    public Cart_Controller(Cart_ServiceIn cartService) {
        this.cartService = cartService;
    }

    // Phương thức helper để tạo Pageable
    private Pageable createPageable(int page, int size, String sort) {
        if (sort != null) {
            // sort format: "field,asc" or "field,desc"
            String[] sortParts = sort.split(",");
            Sort.Direction direction = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            return PageRequest.of(page, size, Sort.by(direction, sortParts[0]));
        }
        return PageRequest.of(page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?>getAllCart(@PathVariable int id){
        List<Display_Cart_All_DTO> dtos=cartService.getAllCartFromUser(id);
        if(dtos.isEmpty()){
            return ResponseEntity.ok("Danh sách giỏ hàng rỗng");
        }
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/by-id/{id}")
    public ResponseEntity<?>getCartById(@PathVariable int id){
        Display_Cart_DTO dto=cartService.findCartById(id);
        return ResponseEntity.ok(dto);
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
