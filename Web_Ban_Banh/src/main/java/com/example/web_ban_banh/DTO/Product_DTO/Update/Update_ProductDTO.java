package com.example.web_ban_banh.DTO.Product_DTO.Update;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Update_ProductDTO {
    @NotBlank(message = "Không được để trống Tên Sản Phẩm")
    @Length(max=255,message = "Tên Sản Phẩm không đươc quá 255 ký tự")
    private String productname;

    @NotBlank(message = "Không được để trống mô tả sản phẩm")
    @Length(max=1000,message = "Mô Tả sản phẩm không được quá 1000 ký tự")
    private String describe;

    @PositiveOrZero(message = "Số Lượng phải từ 0 trở lên")
    private Integer quantity;

    @Positive(message = "Giá Gốc phải lớn hơn 0")
    @Digits(integer = 10,fraction = 3,message = "Giá gốc có định dạng số thập phân (10000,999)")
    private Double originalPrice;

    @Positive(message = "Giá Khuyến Mãi phải lớn hơn 0")
    @Digits(integer = 10,fraction = 3,message = "Giá Khuyến Mãi có định dạng số thập phân (10000,999)")
    private Double promotionalPrice;

    @NotBlank(message = "Không được để trống Slug")
    @Length(max = 255,message = "Slug không được vượt quá 255 ký tự")
    private String slug;

    private boolean isNew;

    private MultipartFile imgFile;
    private List<String> product_size;
    private String category;
}
