package com.example.web_ban_banh.DTO.Cart_DTO.Get;

import com.example.web_ban_banh.DTO.Cart_Details_DTO.Get.Cart_DetailsDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart_DTO {
    private int cartId;
    private List<Cart_DetailsDTO> items;
    private Double originalTotal;
    private Double promotionalTotal;
}
