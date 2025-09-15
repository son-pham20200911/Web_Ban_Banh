package com.example.web_ban_banh.DTO.Product_DTO.Get;

import com.example.web_ban_banh.DTO.Product_size_DTO.Get.ProductSize_Order_DetailsDTO;
import com.example.web_ban_banh.DTO.Product_size_DTO.Get.Product_size_DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product_Order_DetailsDTO {
    private String productname;
    private List<ProductSize_Order_DetailsDTO> productSizes;
}
