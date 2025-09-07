package com.example.web_ban_banh.DTO.Discount_Code_DTO.Create;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Create_Discount_Code_DTO {
    @NotNull(message = "Không được để trống giá trị mã giảm giá")
    @Positive(message = "Không dược điền số âm")
    @Min(value = 1000,message = "Giá trị của mã giảm giá phải từ 1000 trở lên")
    private Double value;

    @NotBlank(message = "Không được để trống Mã")
    @Length(max = 255,message = "Mã không được quá 255 ký tự")
    private String code;

    @NotNull(message = "Không được để trống Ngày bắt đầu")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date startDate;

    @NotNull(message = "Không được để trống Ngày kết thúc")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date endDate;

    private boolean activated;
}
