package com.example.web_ban_banh.Controller.Category_Controller;

import com.example.web_ban_banh.DTO.Category_DTO.Create.Create_Category_DTO;
import com.example.web_ban_banh.DTO.Category_DTO.Get.Category_DTO;
import com.example.web_ban_banh.DTO.Category_DTO.Update.Update_Category_DTO;
import com.example.web_ban_banh.Service.Category_Service.Category_ServiceIn;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Validated
public class Category_Controller {
    private Category_ServiceIn categoryService;

    @Autowired
    public Category_Controller(Category_ServiceIn categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/")
    public ResponseEntity<?>getAllCategory(){
        List<Category_DTO>categorys=categoryService.getAllCategory();
        if(categorys.isEmpty()){
            return ResponseEntity.ok("Danh sách rỗng");
        }
        return ResponseEntity.ok(categorys);
    }

    @PostMapping("/")
    public ResponseEntity<?>createCategory( @RequestBody @Valid Create_Category_DTO dto){
        Category_DTO categoryDTO=categoryService.createCategory(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Tạo thành công thể loại mới");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?>updateCategory(@PathVariable int id, @RequestBody @Valid Update_Category_DTO dto){
        Category_DTO categoryDto=categoryService.updateCategory(id,dto);
        return ResponseEntity.ok("Cập nhật thành công thể loại "+dto.getCategoryName());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?>deleteCategory(@PathVariable int id){
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Đã xóa thành công thể loại có Id: "+id);
    }
}
