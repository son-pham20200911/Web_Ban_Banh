package com.example.web_ban_banh.DTO.Cart_DTO.Get;

import com.example.web_ban_banh.DTO.Cart_Details_DTO.Get.Display_Cart_DetailsDTO;
import com.example.web_ban_banh.Entity.CartStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Display_Cart_All_DTO {
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date cartingDate;
    private Double originalPrice;
    private Double promotionalPrice;
    private CartStatus status;
}
