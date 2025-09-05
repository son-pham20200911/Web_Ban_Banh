package com.example.web_ban_banh.DTO.Cart_DTO.Create;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Create_CartDTO {
    @Min(value = 1,message = "ID sản phẩm phải lớn hơn 0")
    private Integer productId;

    @Min(value = 1,message = "ID kích thước sản phẩm phải lớn hơn 0")
    private Integer productSizeId;

    @Min(value = 1,message = "Số lượng sản phẩm phải lớn hơn 0")
    private int quantity;
}
