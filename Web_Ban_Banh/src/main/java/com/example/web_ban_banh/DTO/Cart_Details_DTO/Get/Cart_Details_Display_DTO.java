package com.example.web_ban_banh.DTO.Cart_Details_DTO.Get;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart_Details_Display_DTO {
    private int quantity;
    double originalPrice;
    double promotionalPrice;
}
