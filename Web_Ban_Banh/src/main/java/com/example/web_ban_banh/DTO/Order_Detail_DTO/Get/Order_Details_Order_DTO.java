package com.example.web_ban_banh.DTO.Order_Detail_DTO.Get;

import com.example.web_ban_banh.DTO.Product_DTO.Get.Product_CartDTO;
import com.example.web_ban_banh.DTO.Product_DTO.Get.Product_Order_DetailsDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order_Details_Order_DTO {
    private Product_Order_DetailsDTO product;
    private int quantity;
}
