package com.example.web_ban_banh.DTO.Product_size_DTO.Create;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Create_Product_size_DTO {
    private int id;
    @NotBlank(message = "Không được để trống Label")
    @Length(max=255, message = "Label không được quá 255 ký tự")
    private String label;

    @NotNull(message = "Không được để trống Giá Gốc")
    @Digits(integer = 10,fraction = 3,message = "Nhập đúng định dạng số thập phân")
    private Double originalPrice;

    @NotNull(message = "Không được để trống Giá Khuyến Mãi")
    @Digits(integer = 10,fraction = 3,message = "Nhập đúng định dạng số thập phân")
    private Double promotionalPrice;

    @NotNull(message = "Không được để trống Số Lượng")
    @Min(value = 0,message = "Số lượng không được bé hơn 0")
    private Integer quantity;
}
