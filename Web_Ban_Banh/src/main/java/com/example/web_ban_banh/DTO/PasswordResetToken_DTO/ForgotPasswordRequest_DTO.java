package com.example.web_ban_banh.DTO.PasswordResetToken_DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequest_DTO {
    @NotBlank(message = "Không được để trống Email")
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_-]+(@gmail.com)$",message = "Email phải đúng định dạng ...@gmail.com")
    private String email;
}
