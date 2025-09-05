package com.example.web_ban_banh.Controller.Product_size_Controller;

import com.example.web_ban_banh.DTO.Product_DTO.Update.Update_ProductDTO;
import com.example.web_ban_banh.DTO.Product_size_DTO.Create.Create_Product_size_DTO;
import com.example.web_ban_banh.DTO.Product_size_DTO.Get.Product_size_DTO;
import com.example.web_ban_banh.DTO.Product_size_DTO.Update.Update_Product_size_DTO;
import com.example.web_ban_banh.Service.Product_size_Service.Product_Size_ServiceIn;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productsize")
@Validated
public class product_Size_Controller {
    private Product_Size_ServiceIn productSizeService;

    @Autowired
    public product_Size_Controller(Product_Size_ServiceIn productSizeService) {
        this.productSizeService = productSizeService;
    }

    @GetMapping("/")
    public ResponseEntity<?>getAllProductSize(){
        List<Product_size_DTO>dto=productSizeService.getAllProductSize();
        if(dto.isEmpty()){
            return ResponseEntity.ok("Danh sách rỗng");
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?>findProductSizeById(@PathVariable @Min(value = 1, message = "ID phải lớn hơn 0")int id){
        Product_size_DTO dto=productSizeService.findProductSizeById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/label")
    public ResponseEntity<?>findProductSizeByLabel(@RequestParam String label){
        List<Product_size_DTO>dto=productSizeService.findProductSizeByLabel(label);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/")
    public ResponseEntity<?>createProductSize(@RequestBody @Valid Create_Product_size_DTO dto){
        Create_Product_size_DTO productSizeDTO=productSizeService.createProductSize(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Đã tạo thành công Kích Thước sản phẩm: "+dto.getLabel());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?>updateProductSize(@PathVariable @Min(value = 1, message = "ID phải lớn hơn 0")int id, @RequestBody @Valid Update_Product_size_DTO dto){
        Update_Product_size_DTO productSizeDTO=productSizeService.updateProductSize(id,dto);
        return ResponseEntity.ok("Đã cập nhật thành công Kích Thước sản phẩm: "+dto.getLabel());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?>deleteProductSize(@PathVariable @Min(value = 1,message = "ID phải lớn hơn 0")int id){
        productSizeService.deleteProductSize(id);
        return ResponseEntity.ok("Đã xóa thành công Kích thước sản phẩm có ID: "+id);
    }
}
