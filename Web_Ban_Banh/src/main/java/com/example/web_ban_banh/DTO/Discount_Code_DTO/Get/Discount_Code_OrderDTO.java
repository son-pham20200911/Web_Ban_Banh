package com.example.web_ban_banh.DTO.Discount_Code_DTO.Get;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Discount_Code_OrderDTO {
    private Double value;
    private String code;
}
