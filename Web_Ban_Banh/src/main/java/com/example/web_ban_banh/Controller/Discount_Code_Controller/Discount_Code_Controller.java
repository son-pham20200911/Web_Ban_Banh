package com.example.web_ban_banh.Controller.Discount_Code_Controller;

import com.example.web_ban_banh.DTO.Discount_Code_DTO.Create.Create_Discount_Code_DTO;
import com.example.web_ban_banh.DTO.Discount_Code_DTO.Get.Discount_CodeDTO;
import com.example.web_ban_banh.DTO.Discount_Code_DTO.Update.Update_Discount_Code_DTO;
import com.example.web_ban_banh.Service.Discount_code_Service.Discount_Code_ServiceIn;
import jakarta.validation.Valid;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/discountcode")
@Validated
public class Discount_Code_Controller {
    private Discount_Code_ServiceIn discountCodeService;

    @Autowired
    public Discount_Code_Controller(Discount_Code_ServiceIn discountCodeService) {
        this.discountCodeService = discountCodeService;
    }

    @GetMapping("/")
    public ResponseEntity<?>getAllDiscountCode(){
        List<Discount_CodeDTO>dtos=discountCodeService.getAllDiscountCode();
        if(dtos.isEmpty()){
            return ResponseEntity.ok("Danh sách rỗng");
        }
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/findcode")
    public ResponseEntity<?>findByCode(@RequestParam @Length(min = 1,message = "Hãy điền ít nhất 1 ký tự") String code){
        Discount_CodeDTO discountCodeDTO=discountCodeService.getDiscountCodeByCode(code);
        return ResponseEntity.ok(discountCodeDTO);
    }

    @PostMapping("/")
    public ResponseEntity<?>createDiscountCode(@RequestBody @Valid Create_Discount_Code_DTO dto){
        Discount_CodeDTO discountCodeDTO=discountCodeService.createDiscountCode(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Tạo mới mã giảm giá thành công");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?>updateDiscountCode(@PathVariable int id, @RequestBody @Valid Update_Discount_Code_DTO update){
        Discount_CodeDTO discountCodeDTO=discountCodeService.updateDiscountCode(id,update);
        return ResponseEntity.ok("Cập nhật mã giảm giá "+update.getCode()+" thành công");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?>deleteDiscountCOde(@PathVariable int id){
        discountCodeService.deleteDiscountCode(id);
        return ResponseEntity.ok("Đã xóa thành công mã giảm giá có Id: "+id);
    }
}
