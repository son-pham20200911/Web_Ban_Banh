package com.example.web_ban_banh.DTO.Cart_Details_DTO.Get;

import com.example.web_ban_banh.DTO.Product_DTO.Get.Product_CartDTO;
import com.example.web_ban_banh.DTO.Product_size_DTO.Get.ProductSize_CartDTO;
import com.example.web_ban_banh.Entity.Product;
import com.example.web_ban_banh.Entity.Product_size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Display_Cart_DetailsDTO {
    private int id;
    private Product_CartDTO product;
    private ProductSize_CartDTO productSize;
}
