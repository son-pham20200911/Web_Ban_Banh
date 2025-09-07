package com.example.web_ban_banh.DTO.Category_DTO.Create;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Create_Category_DTO {
    @NotBlank(message = "Tên thể loại không được để trống")
    @Length(max = 255,message = "Tên thể loại không được quá 255 ký tự")
    private String categoryName;
}
