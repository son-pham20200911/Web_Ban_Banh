package com.example.web_ban_banh.DTO.Product_DTO.Get;

import com.example.web_ban_banh.DTO.Product_size_DTO.Get.Product_size_DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductHideProductSizeDTO {
    private int id;
    private String productname;
    private String description;
    private Integer quantity;
    private Double originalPrice;
    private Double promotionalPrice;
    private String img;
}
