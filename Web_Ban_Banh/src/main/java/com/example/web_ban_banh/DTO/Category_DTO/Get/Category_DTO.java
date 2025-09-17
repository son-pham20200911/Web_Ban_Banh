package com.example.web_ban_banh.DTO.Category_DTO.Get;

import com.example.web_ban_banh.DTO.Product_DTO.Get.ProductDTO;
import com.example.web_ban_banh.DTO.Product_DTO.Get.ProductHideProductSizeDTO;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category_DTO {
    private int id;
    private String categoryName;
    private String slug;
    private List<ProductDTO> products;
}
