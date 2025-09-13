package com.example.web_ban_banh.Controller.Product_Controller;

import com.example.web_ban_banh.DTO.Product_DTO.Create.Create_ProductDTO;
import com.example.web_ban_banh.DTO.Product_DTO.Get.ProductDTO;
import com.example.web_ban_banh.DTO.Product_DTO.Get.ProductHideProductSizeDTO;
import com.example.web_ban_banh.DTO.Product_DTO.Update.Update_ProductDTO;
import com.example.web_ban_banh.Entity.Product;
import com.example.web_ban_banh.Service.Product_Service.Product_ServiceIn;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/product")
@Validated
public class Product_Controller {
    private Product_ServiceIn productService;

    @Autowired
    public Product_Controller(Product_ServiceIn productService) {
        this.productService = productService;
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

    @GetMapping("/")
    public ResponseEntity<?> getAllProduct(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "6") int size,
                                           @RequestParam(required = false) String sort) {
        Pageable pageable = createPageable(page, size, sort);
        Page<ProductDTO> dto = productService.getAllProduct(pageable);
        if (dto.isEmpty()) {
            return ResponseEntity.ok().body("Danh sách rỗng");
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable @Min(value = 1, message = "Id phải lớn hơn 0") int id) {
        ProductDTO productDTO = productService.findProductById(id);
        return ResponseEntity.ok(productDTO);
    }

    @GetMapping("/productname")
    public ResponseEntity<?> getProductByProductName(@RequestParam String productName,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "6") int size,
                                                     @RequestParam(required = false) String sort) {
        Pageable pageable=createPageable(page,size,sort);
        Page<ProductDTO> dtos = productService.findProductByProductName(productName, pageable);
        return ResponseEntity.ok(dtos);
    }


    @GetMapping("/betweenPriceImprove")
    public ResponseEntity<?> getProductBetWeenPriceImprove(@RequestParam @Min(value = 0, message = "Giá nhỏ nhất không được bé hơn 0") double a, @RequestParam double b) {
        List<ProductDTO> productDTOs = productService.findProductBetweenPriceImprove(a, b);
        if (productDTOs.isEmpty()) {
            return ResponseEntity.ok("Không tìm thấy Sản Phẩm mà bạn muốn tìm");
        }
        return ResponseEntity.ok(productDTOs);
    }


    @GetMapping("/ztoa")
    public ResponseEntity<?> zToA() {
        List<ProductDTO> productDTO = productService.zToA();
        if (productDTO.isEmpty()) {
            return ResponseEntity.ok("Danh sách rỗng");
        }
        return ResponseEntity.ok(productDTO);
    }

    @GetMapping("/atoz")
    public ResponseEntity<?> aToZ() {
        List<ProductDTO> productDTO = productService.atoZ();
        if (productDTO.isEmpty()) {
            return ResponseEntity.ok("Danh sách rỗng");
        }
        return ResponseEntity.ok(productDTO);
    }

    @GetMapping("/pricehightolow")
    public ResponseEntity<?> priceHighToLow() {
        List<ProductDTO> productsDTO = productService.highToLow();
        if (productsDTO.isEmpty()) {
            return ResponseEntity.ok("Danh sách rỗng");
        }
        return ResponseEntity.ok(productsDTO);
    }

    @GetMapping("/pricelowtohigh")
    public ResponseEntity<?> priceLowToHigh() {
        List<ProductDTO> productsDTO = productService.lowToHight();
        if (productsDTO.isEmpty()) {
            return ResponseEntity.ok("Danh sách rỗng");
        }
        return ResponseEntity.ok(productsDTO);
    }

    @PostMapping("/")
    public ResponseEntity<?> createProduct(@ModelAttribute @Valid Create_ProductDTO dto) {
        ProductDTO productDTO = productService.createProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Tạo thành công Sản Phẩm " + productDTO.getProductname());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@ModelAttribute @Valid Update_ProductDTO dto, @PathVariable @Min(value = 1, message = "ID phải lớn hơn 0") int id) throws IOException {
        ProductDTO productDTO = productService.updateProduct(id, dto);
        return ResponseEntity.ok("Đã cập nhật thành công Sản Phẩm: " + productDTO.getProductname());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable @Min(value = 1, message = "ID phải lớn hơn 0") int id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Đã xóa thành công Sản Phẩm có ID: " + id);
    }
}
