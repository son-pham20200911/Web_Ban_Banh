package com.example.web_ban_banh.DTO.Order_Detail_DTO.Get;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order_Details_DTO {
    private int productId;
    private String productName;
    private int productSizeId;
    private String productLabel;
    private int quantity;
    private Double originalPrice;
    private Double promotionalPrice;
}
