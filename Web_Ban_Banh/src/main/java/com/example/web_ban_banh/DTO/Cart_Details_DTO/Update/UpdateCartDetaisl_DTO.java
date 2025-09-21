package com.example.web_ban_banh.DTO.Cart_Details_DTO.Update;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCartDetaisl_DTO {
    @NotNull(message = "Không được để trống số lượng sản phẩm")
    @Min(value = 1,message = "Số lượng phải lớn hơn 0")
    private int productQuantity;
}
