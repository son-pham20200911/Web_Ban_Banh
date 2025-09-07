package com.example.web_ban_banh.DTO.Category_DTO.Update;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Update_Category_DTO {
    @NotBlank(message = "Tên thể loại không được để trống")
    @Length(max = 255,message = "Tên thể loại không được quá 255 ký tự")
    private String categoryName;

    private List<String> products;
}
