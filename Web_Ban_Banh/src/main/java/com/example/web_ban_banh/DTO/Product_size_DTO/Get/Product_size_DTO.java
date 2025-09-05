package com.example.web_ban_banh.DTO.Product_size_DTO.Get;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product_size_DTO {
    private int id;
    private String label;
    private Double originalPrice;
    private Double promotionalPrice;
    private Integer quantity;
}
